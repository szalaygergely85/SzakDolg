package com.example.szakdolg.chat.helper;

import android.content.Context;

import com.example.szakdolg.chat.adapter.ChatAdapter;
import com.example.szakdolg.db.util.DatabaseUtil;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.entity.User;

public class ChatHelper {
    private Long conversationId;
    private User currentUser;

    private String authToken;
    private ChatAdapter chatAdapter;

    private DatabaseUtil databaseUtil;


    public ChatHelper(Context context, Long conversationId, User currentUser, String authToken, ChatAdapter chatAdapter) {
        this.conversationId = conversationId;
        this.currentUser = currentUser;
        this.authToken = authToken;
        this.chatAdapter = chatAdapter;
        this.databaseUtil = new DatabaseUtil(context);
    }

    MessageApiHelper messageApiHelper = new MessageApiHelper();

    public void sendMessage(
            int messageType,
            String encryptedContentSenderVersion,
            String encryptedContentString
    ) {
        MessageEntry messageEntry = new MessageEntry(
                conversationId,
                currentUser.getUserId(),
                System.currentTimeMillis(),
                encryptedContentString,
                messageType,
                encryptedContentSenderVersion
        );

        databaseUtil.insertMessageEntry(messageEntry);

        messageApiHelper.sendMessage(
                conversationId,
                messageEntry,
                chatAdapter,
                authToken
        );
        messageApiHelper.reloadMessages(conversationId, chatAdapter, authToken);
    }
}
