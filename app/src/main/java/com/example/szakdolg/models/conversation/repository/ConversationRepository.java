package com.example.szakdolg.models.conversation.repository;

import com.example.szakdolg.models.conversation.entity.Conversation;

import retrofit2.Callback;

public interface ConversationRepository {
    void addConversation(
            Conversation conversation,
             String token,
            Callback<Conversation> callback
    );

    void getConversation(
            Long id, String token, Callback<Conversation> callback
    );
}
