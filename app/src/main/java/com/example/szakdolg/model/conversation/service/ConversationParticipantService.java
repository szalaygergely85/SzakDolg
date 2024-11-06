package com.example.szakdolg.model.conversation.service;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.conversation.db.ConversationParticipantDatabaseUtil;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantService extends BaseService {

   private ConversationParticipantDatabaseUtil conversationParticipantDatabaseUtil;

   public ConversationParticipantService(Context context, User currentUser) {
      super(context, currentUser);
      this.conversationParticipantDatabaseUtil =
      new ConversationParticipantDatabaseUtil(context, currentUser);
   }

   public List<ConversationParticipant> getOtherParticipants(
      Long conversationId
   ) {
      List<ConversationParticipant> conversationParticipants =
         conversationParticipantDatabaseUtil.getParticipantsByConversationId(
            conversationId
         );
      List<ConversationParticipant> others = new ArrayList<>();
      for (ConversationParticipant participant : conversationParticipants) {
         if (participant.getUserId() != currentUser.getUserId()) {
            others.add(participant);
         }
      }
      return others;
   }

   public void addParticipants(List<ConversationParticipant> participants) {
      if (participants != null) {
         for (ConversationParticipant paticipant : participants) {
            conversationParticipantDatabaseUtil.insertConversationParticipant(
               paticipant
            );
         }
      }
   }
}
