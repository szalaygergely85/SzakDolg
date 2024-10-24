package com.example.szakdolg.chat.helper;

import android.content.Context;
import com.example.szakdolg.chat.adapter.ChatAdapter;
import com.example.szakdolg.conversation.entity.ConversationParticipant;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.model.User;
import com.example.szakdolg.util.UUIDUtil;
import java.util.ArrayList;
import java.util.List;

public class ChatHelper {

   private Long conversationId;
   private User currentUser;

   private String authToken;
   private ChatAdapter chatAdapter;
   private ConversationDatabaseUtil conversationDatabaseUtil;

   private UserDatabaseUtil userDatabaseUtil;

   private MessageDatabaseUtil messageDatabaseUtil;

   private Context context;

   public ChatHelper(
      Context context,
      Long conversationId,
      User currentUser,
      String authToken,
      ChatAdapter chatAdapter
   ) {
      this.context = context;
      this.conversationId = conversationId;
      this.currentUser = currentUser;
      this.authToken = authToken;
      this.chatAdapter = chatAdapter;
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(context, currentUser);
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
         encryptedContentSenderVersion,
         UUIDUtil.UUIDGenerator()
      );

      messageApiHelper.sendMessage(
         context,
         conversationId,
         messageEntry,
         chatAdapter,
         authToken,
         currentUser
      );
   }

   public List<MessageEntry> getMessages(Long conversationId) {
      return messageDatabaseUtil.getAllMessageEntriesOfConversation(
         conversationId
      );
   }

   public List<User> getUsers(Long conversationId) {
      List<ConversationParticipant> participants =
         conversationDatabaseUtil.getParticipantsByConversationId(
            conversationId
         );
      List<User> users = new ArrayList<>();

      for (ConversationParticipant participant : participants) {
         users.add(userDatabaseUtil.getUserById(participant.getUserId()));
      }

      return users;
   }
}
