package com.example.szakdolg.activity.chat.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.models.message.constans.MessageTypeConstants;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.conversation.ConversationParticipantCoordinatorService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.message.MessageCoordinatorService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserCoordinatorService;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.UUIDUtil;
import java.util.ArrayList;
import java.util.List;

public class NewChatActivityHelper {

   private User currentUser;

   private List<User> contacts;
   private AppCompatActivity context;
   private ConversationCoordinatorService conversationCoordinatorService;
   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;

   private UserCoordinatorService userCoordinatorService;

   private MessageCoordinatorService messageCoordinatorService;

   public NewChatActivityHelper(AppCompatActivity context, User currentUser) {
      this.context = context;
      this.conversationCoordinatorService =
      new ConversationCoordinatorService(context, currentUser);
      this.currentUser = currentUser;
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
      this.userCoordinatorService = new UserCoordinatorService(context);
      this.messageCoordinatorService =
      new MessageCoordinatorService(context, currentUser);

      this.contacts = userCoordinatorService.getContacts(currentUser);
   }

   public List<User> getContacts() {
      return contacts;
   }

   public void sendMessage(String recipients, String messageContent) {
      List<User> users = new ArrayList<>();
      for (String recipient : recipients.split(", ")) {
         Long id = Long.valueOf(_extractIdFromDisplayName(recipient.trim()));
         if (id != null) {
            users.add(userCoordinatorService.getUserByUserId(id, currentUser));
         }
      }
      users.add(currentUser);
      if (users.size() > 2) {} else {
         String encryptedContentString = EncryptionHelper.encrypt(
            messageContent,
            CacheUtil.getPublicKeyFromCache(context, users.get(0).getEmail())
         );

         MessageEntry messageEntry = new MessageEntry(
            null,
            null,
            currentUser.getUserId(),
            System.currentTimeMillis(),
            encryptedContentString,
            false,
            MessageTypeConstants.MESSAGE,
            messageContent,
            UUIDUtil.UUIDGenerator()
         );

         conversationCoordinatorService.addConversation(
            new Conversation(
               null,
               System.currentTimeMillis(),
               currentUser.getUserId(),
               users.size()
            ),
            users,
            messageEntry
         );
      }
   }

   private String _extractIdFromDisplayName(String displayName) {
      for (User user : contacts) {
         if (user.getDisplayName().equals(displayName)) {
            return user.getUserId().toString();
         }
      }
      return null;
   }
}
