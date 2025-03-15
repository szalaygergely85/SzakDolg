package com.example.szakdolg.models.conversation.repository;

import android.content.Context;

import com.example.szakdolg.models.conversation.api.ConversationApiService;
import com.example.szakdolg.models.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationRepositoryImpl implements ConversationRepository {

    private final Context context;
    private final User currentUser;
    private final ConversationDatabaseUtil conversationDatabaseUtil;
    private final ConversationApiService conversationApiService;

    public ConversationRepositoryImpl(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
        this.conversationDatabaseUtil = new ConversationDatabaseUtil(context, currentUser);
        this.conversationApiService = RetrofitClient.getRetrofitInstance().create(ConversationApiService.class);

    }



    @Override
    public void addConversation(Conversation conversation, String token, Callback<Conversation> callback) {

        //TODO Check locally if Conversation exists

        conversationApiService.addConversation(conversation, token).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {

                conversationDatabaseUtil.insertConversation(response.body());

                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch contact"));
            }
        });

    }

    @Override
    public void addConversationByUserId(List<Long> userIds, String token, Callback<Conversation> callback) {
        conversationApiService.addConversationByUserId(userIds, token).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                conversationDatabaseUtil.insertConversation(response.body());

                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch contact"));
            }
        });
    }

    @Override
    public void getConversation(Long id, String token, Callback<Conversation> callback) {
        Conversation localConversation = conversationDatabaseUtil.getConversationById(id);
        if (localConversation != null) {
            callback.onResponse(null, Response.success(localConversation));
        } else {
            conversationApiService.getConversation(id, token).enqueue(new Callback<Conversation>() {
                @Override
                public void onResponse(Call<Conversation> call, Response<Conversation> response) {

                    conversationDatabaseUtil.insertConversation(response.body());

                    callback.onResponse(call, response);
                }

                @Override
                public void onFailure(Call<Conversation> call, Throwable throwable) {
                    callback.onFailure(call, new Throwable("Failed to fetch contact"));
                }
            });
        }

    }
}
