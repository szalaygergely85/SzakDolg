package com.example.szakdolg.conversation;

import static com.example.szakdolg.constans.SharedPreferencesConstans.CONVERSATION_ID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.szakdolg.activity.ChatActivity;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.IntentUtil;

import java.util.ArrayList;
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
                        Long conversationId = response.body();

                        //TODO meg kell csinalni a masik oldalt

                        Bundle extra = new Bundle();
                        extra.putLong(SharedPreferencesConstans.CONVERSATION_ID, conversationId);
                        extra.putSerializable(SharedPreferencesConstans.LOGGED_USER, loggedUser);

                        IntentUtil.startActivity(context, ChatActivity.class, extra);




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
            intent.putExtra("participant_user", new ArrayList<>(participants));
            intent.putExtra(SharedPreferencesConstans.LOGGED_USER, loggedUser);
            context.startActivity(intent);

        }





    }

    public void getConversationKeyStatus(Long conversationId, User loggedUser) {

        Call<Long> call = conversationApiService.getConversationKeyStatus(conversationId, loggedUser.getUserId());
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Long keyStatus= response.body();
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }
}
