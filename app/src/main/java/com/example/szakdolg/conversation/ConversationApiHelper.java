package com.example.szakdolg.conversation;

import android.util.Log;

import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationApiHelper {
    private final String TAG ="ConversationApiHelper";

    private MessageApiHelper messageApiHelper = new MessageApiHelper();

    private ConversationApiService conversationApiService = RetrofitClient.getRetrofitInstance().create(ConversationApiService.class);

    public void addConversation(List<User> participants, ChatAdapter adapter) {
        Call<Long> call = conversationApiService.addConversation(participants);

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Log.e(TAG, "" + response.code());

                if (response.isSuccessful()) {
                    Long conversationId = response.body();
                    Log.e(TAG, "" + response.body());
                    messageApiHelper.reloadMessages(conversationId, adapter);

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
    }
}