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
        MessageEntry messageEntry = messageDatabaseUtil.getLatestMessageEntry(conversationId);
        if (messageEntry != null) {
            if (System.currentTimeMillis() - messageEntry.getTimestamp() > AppConstants.MESSAGE_SYNC_TIME) {
                callback.onResponse(null, Response.success(messageEntry));
                return;
            }

        }
        messageApiService.getLatestMessage(token, conversationId).enqueue(callback);
    }
}
