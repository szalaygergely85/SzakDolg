package com.example.szakdolg.models.conversation;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.conversation.api.ConversationApiHelper;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.message.MessageCoordinatorService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserCoordinatorService;
import java.util.ArrayList;
import java.util.List;

public class ConversationCoordinatorService extends BaseService {

   private MessageCoordinatorService messageCoordinatorService;
   private UserCoordinatorService userCoordinatorService;
   private ConversationService conversationService;

   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;
   private ConversationApiHelper conversationApiHelper;

   public ConversationCoordinatorService(Context context, User currentUser) {
      super(context, currentUser);
      this.conversationService = new ConversationService(context, currentUser);
      this.conversationApiHelper =
      new ConversationApiHelper(context, currentUser);
      this.messageCoordinatorService =
      new MessageCoordinatorService(context, currentUser);
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
      this.userCoordinatorService = new UserCoordinatorService(context);
   }

   public Conversation getConversation(Long conversationId) {
      Conversation conversation = conversationService.getConversation(
         conversationId
      );
      if (conversation == null) {
         conversationApiHelper.getConversation(
            conversationId,
            conversationRemote -> {
               if (conversationRemote != null) {
                  conversationService.addConversation(conversationRemote);
               }
            }
         );
         return null;
      } else {
         return conversation;
      }
   }

   public List<Conversation> getAllValidConversations(Runnable onSuccess) {
      List<Conversation> conversations =
         conversationService.getAllConversations();
      if (conversations.size() > 0) {
         List<Conversation> validConversations = _getValidConversations(
            conversations
         );
         if (validConversations.size() > 0) {
            return conversations;
         } else {
            return null;
         }
      } else {
         conversationApiHelper.getAllConversation(conversationsRemote -> {
            conversationService.addConversations(conversationsRemote);
            if (onSuccess != null) {
               onSuccess.run();
            }
         });
         return null;
      }
   }

   private List<Conversation> _getValidConversations(
      List<Conversation> conversations
   ) {
      List<Conversation> validConversation = new ArrayList<>();
      for (Conversation conversation : conversations) {
         if (validateConversationById(conversation.getConversationId())) {
            validConversation.add(conversation);
         }
      }
      return validConversation;
   }

   public void addConversation(
      Conversation conversation,
      List<User> users,
      MessageEntry messageEntry
   ) {
      conversationApiHelper.addConversation(
         conversation,
         newConversation -> {
            conversationService.addConversation(newConversation);
            conversationParticipantCoordinatorService.addConversationsParticipants(
               users,
               newConversation
            );
            messageEntry.setConversationId(newConversation.getConversationId());
            messageCoordinatorService.sendMessage(messageEntry);
         }
      );
   }

   public boolean validateConversationById(Long conversationId) {
      Conversation conversation = conversationService.getConversation(
         conversationId
      );
      if (conversation != null) {
         List<ConversationParticipant> conversationParticipants =
            conversationParticipantCoordinatorService.getOtherParticipants(
               conversation.getConversationId()
            );
         List<User> users =
            userCoordinatorService.getConversationParticipantUser(
               conversationParticipants,
               currentUser
            );
         List<MessageEntry> messageEntries =
            messageCoordinatorService.getMessagesByConversationId(
               conversation.getConversationId()
            );
         if (messageEntries != null && users != null) {
            return !messageEntries.isEmpty() && !users.isEmpty();
         }
      }

      return false;
   }
}
