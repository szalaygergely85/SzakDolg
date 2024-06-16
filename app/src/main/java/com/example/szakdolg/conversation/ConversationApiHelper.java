package com.example.szakdolg.conversation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.activity.ChatActivity;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.retrofit.CustomCallback;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.util.KeyStoreUtil;

import java.security.PublicKey;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationApiHelper {
    private final String TAG ="ConversationApiHelper";

    private MessageApiHelper messageApiHelper = new MessageApiHelper();

    private ConversationApiService conversationApiService = RetrofitClient.getRetrofitInstance().create(ConversationApiService.class);

    public void openConversation(Context context, Long conversationId, List<User> participants, User loggedUser) {
        if(conversationId==null && participants!=null){
            Call<Long> call = conversationApiService.addConversation(participants);
            call.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.e(TAG, "" + response.code());

                    if (response.isSuccessful()) {
                        Intent intent = new Intent(context, ChatActivity.class);

                        intent.putExtra(SharedPreferencesConstans.CONVERSATION_ID, response.body());
                        intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);

                        context.startActivity(intent);




                    } else {
                        Log.e(TAG, "" + response.code());
                        //TODO Handle the error
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.e(TAG, "" + t.getMessage());
                }
            });

        }else{
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(SharedPreferencesConstans.CONVERSATION_ID, conversationId);
            intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
            context.startActivity(intent);

        }





    }

    public void getConversationAndContentById(Long conversationId, Long currentUserId, Button btnSend, CustomCallback<User> callback) {
        Call<ConversationContent> call = conversationApiService.getConversationAndContentById(conversationId);
        call.enqueue(new Callback<ConversationContent>() {
            @Override
            public void onResponse(Call<ConversationContent> call, Response<ConversationContent> response) {
                if (response.isSuccessful()) {
                    ConversationContent conversationContent = response.body();
                    List<User> allParticipants = conversationContent.getParticipants();
                    //TODO must be different with groupchats
                    User otherUser = UserUtil.removeCurrentUserFromList(conversationContent.getParticipants(), currentUserId);
                    callback.onSuccess(otherUser);
                    try {
                        //TODO must be different with groupchats

                        PublicKey publicKey = KeyStoreUtil.getPublicKey(otherUser.getEmail());

                        if (publicKey != null) {
                            btnSend.setActivated(true);
                        } else {
                            KeyStoreUtil.savePublicKey(conversationContent.getPublicKey(), otherUser.getEmail());
                            btnSend.setActivated(true);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    callback.onError(new Exception("Failed to fetch conversation content"));
                }
            }
            @Override
            public void onFailure(Call<ConversationContent> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });

    }
}