package com.example.szakdolg.models.conversation;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.conversation.api.ConversationParticipantApiHelper;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.conversation.service.ConversationParticipantService;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantCoordinatorService extends BaseService {

   private ConversationParticipantService conversationParticipantService;
   private ConversationParticipantApiHelper conversationParticipantApiHelper;

   public ConversationParticipantCoordinatorService(
      Context context,
      User currentUser
   ) {
      super(context, currentUser);
      this.conversationParticipantService =
      new ConversationParticipantService(context, currentUser);
      this.conversationParticipantApiHelper =
      new ConversationParticipantApiHelper(context, currentUser);
   }

   public List<ConversationParticipant> getOtherParticipants(
      Long conversationId
   ) {
      List<ConversationParticipant> participants =
         conversationParticipantService.getOtherParticipants(conversationId);
      if (participants.size() < 1) {
         conversationParticipantApiHelper.getParticipants(
            conversationId,
            participantsRemote -> {
               if (participantsRemote != null) {
                  conversationParticipantService.addParticipants(
                     participantsRemote
                  );
               }
            }
         );
         return null;
      } else {
         return participants;
      }
   }

   public void addConversationsParticipants(
      List<User> users,
      Conversation conversation
   ) {
      List<ConversationParticipant> conversationParticipants =
         new ArrayList<>();
      for (User user : users) {
         conversationParticipants.add(
            new ConversationParticipant(
               conversation.getConversationId(),
               user.getUserId()
            )
         );
      }
      addConversationsParticipants(conversationParticipants);
   }

   private void addConversationsParticipants(
      List<ConversationParticipant> conversationParticipants
   ) {
      conversationParticipantService.addParticipants(conversationParticipants);
      conversationParticipantApiHelper.addParticipants(
         conversationParticipants
      );
   }
}
