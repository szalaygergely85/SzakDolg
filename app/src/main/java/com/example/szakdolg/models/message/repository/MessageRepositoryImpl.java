package com.example.szakdolg.models.message.repository;

import android.content.Context;

import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.conversation.api.ConversationApiService;
import com.example.szakdolg.models.message.MessageDatabaseUtil;
import com.example.szakdolg.models.message.api.MessageApiService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageRepositoryImpl implements MessageRepository {


    private final Context context;
    private final User currentUser;
    private final MessageDatabaseUtil messageDatabaseUtil;
    private final MessageApiService messageApiService;


    public MessageRepositoryImpl(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
        this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
        this.messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);
    }

    @Override
    public void getLatestMessage(String token, Long conversationId, Callback<MessageEntry> callback) {
        MessageEntry localMessage = messageDatabaseUtil.getLatestMessageEntry(conversationId);
        if (localMessage != null) {
            if (System.currentTimeMillis() - localMessage.getTimestamp() > AppConstants.MESSAGE_SYNC_TIME) {
                callback.onResponse(null, Response.success(localMessage));
                return;
            }

        }
        messageApiService.getLatestMessage(token, conversationId).enqueue(new Callback<MessageEntry>() {
            @Override
            public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageEntry messageEntry = response.body();
                    if (messageEntry != null) {
                        messageDatabaseUtil.insertMessageEntry(messageEntry);
                    }
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Failed to fetch conversation"));
                }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void addMessage(MessageEntry messageEntry, Callback<MessageEntry> callback) {
        messageApiService.addMessage(messageEntry, currentUser.getToken()).enqueue(new Callback<MessageEntry>() {
            @Override
            public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageEntry messageEntry = response.body();
                    messageEntry.setUploaded(true);
                    if (messageEntry != null) {
                        messageDatabaseUtil.insertMessageEntry(messageEntry);
                    }
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Failed to fetch conversation"));
                }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void addMessages(List<MessageEntry> messageEntries, Callback<MessageEntry> callback) {
        //TODO add messages
    }

    @Override
    public void getMessages(String token, Long conversationId, Callback<List<MessageEntry>> callback) {
        messageApiService.getMessages(currentUser.getToken(), conversationId).enqueue(new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(Call<List<MessageEntry>> call, Response<List<MessageEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                   for (MessageEntry messageEntry : response.body()){
                       if (messageEntry != null) {
                           messageDatabaseUtil.insertMessageEntry(messageEntry);
                       }
                   }
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Failed to fetch conversation"));
                }
            }

            @Override
            public void onFailure(Call<List<MessageEntry>> call, Throwable throwable) {

            }
        });
    }
}
