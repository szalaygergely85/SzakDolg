package com.example.szakdolg.models.conversation;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.conversation.api.ConversationApiHelper;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.message.MessageCoordinatorService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;

public class ConversationCoordinatorService extends BaseService {

   private MessageCoordinatorService messageCoordinatorService;
   private ConversationService conversationService;

   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;
   private ConversationApiHelper conversationApiHelper;

   public ConversationCoordinatorService(Context context, User currentUser) {
      super(context, currentUser);
      this.conversationService = new ConversationService(context, currentUser);
      this.conversationApiHelper =
      new ConversationApiHelper(context, currentUser);

      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
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

   public List<Conversation> getAllConversations(Runnable onSuccess) {
      List<Conversation> conversations =
         conversationService.getAllConversations();
      if (conversations.size() > 0) {
         return conversations;
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
            messageCoordinatorService.addMessage(messageEntry);
         }
      );
   }
}
