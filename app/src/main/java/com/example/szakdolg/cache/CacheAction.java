package com.example.szakdolg.cache;

import android.content.Context;
import com.example.szakdolg.model.message.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class CacheAction {

   public static void validateMessages(
      ArrayList<MessageEntry> messageEntries,
      Context context,
      User user
   ) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );
      List<Long> localMessageIds = messageDatabaseUtil.getAllMessageIds();

      if (messageEntries == null || messageEntries.isEmpty()) {
         throw new IllegalArgumentException(
            "messageEntries should not be null or empty"
         );
      }

      for (MessageEntry messageEntry : messageEntries) {
         if (!localMessageIds.contains(messageEntry.getMessageId())) {
            messageDatabaseUtil.insertMessageEntry(messageEntry);
         }
      }
   }

   public static void validateContacts(
      ArrayList<User> userEntries,
      Context context,
      User currentUser
   ) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );

      if (userEntries == null || userEntries.isEmpty()) {
         throw new IllegalArgumentException(
            "messageEntries should not be null or empty"
         );
      }
      for (User user : userEntries) {
         if (userDatabaseUtil.getUserById(user.getUserId()) == null) {
            userDatabaseUtil.insertUser(user);
         }
      }
   }

   public static void validateConversation(
      List<Conversation> conversations,
      Context context,
      User user
   ) {
      ConversationDatabaseUtil conversationDatabaseUtil =
         new ConversationDatabaseUtil(context, user);
      List<Conversation> localConversations =
         conversationDatabaseUtil.getAllConversations();
      if (conversations == null || conversations.isEmpty()) {
         throw new IllegalArgumentException(
            "conversations should not be null or empty"
         );
      }
      for (Conversation conversation : conversations) {
         if (!localConversations.contains(conversation)) {
            conversationDatabaseUtil.insertConversation(conversation);
         }
      }
   }

   public static void validateConversationParticipant(
      List<ConversationParticipant> participants,
      Context context,
      User user
   ) {
      ConversationDatabaseUtil conversationDatabaseUtil =
         new ConversationDatabaseUtil(context, user);
      List<ConversationParticipant> localParticipants =
         conversationDatabaseUtil.getAllConversationParticipant();
      if (participants == null || participants.isEmpty()) {
         throw new IllegalArgumentException(
            "conversations should not be null or empty"
         );
      }
      for (ConversationParticipant conversationParticipant : participants) {
         if (!localParticipants.contains(conversationParticipant)) {
            conversationDatabaseUtil.insertConversationParticipant(
               conversationParticipant
            );
         }
      }
   }
}
