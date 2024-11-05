package com.example.szakdolg.model.conversation.api;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.user.entity.User;

public class ConversationParticipantApiHelper extends BaseService {

    private ConversationParticipantApiService conversationParticipantApiService = RetrofitClient
            .getRetrofitInstance()
            .create(ConversationParticipantApiService.class);
    public ConversationParticipantApiHelper(Context context, User currentUser) {
        super(context, currentUser);
    }

    public void getParticipants(Long conversationId) {
        conversationParticipantApiService.getConversationParticipants(conversationId, currentUser.getAuthToken());
    }
}
