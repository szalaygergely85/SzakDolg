package com.example.szakdolg.models.message.repository;

import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.message.entity.MessageEntry;

import retrofit2.Call;
import retrofit2.Callback;

public interface MessageRepository {


    void getLatestMessage(
          String token,
           Long conversationId,
          Callback<MessageEntry> callback
    );
}
