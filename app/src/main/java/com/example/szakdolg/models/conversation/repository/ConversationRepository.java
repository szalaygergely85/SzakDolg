package com.example.szakdolg.models.conversation.repository;

import com.example.szakdolg.models.conversation.entity.Conversation;

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;

public interface ConversationRepository {
    void addConversation(
            Conversation conversation,
             String token,
            Callback<Conversation> callback
    );

    void addConversationByUserId(List<Long> userIds,
            String token, Callback<Conversation> callback);

    void getConversation(
            Long id, String token, Callback<Conversation> callback
    );
}
