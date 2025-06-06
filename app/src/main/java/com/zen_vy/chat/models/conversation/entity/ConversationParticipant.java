package com.zen_vy.chat.models.conversation.entity;

import java.io.Serializable;

public class ConversationParticipant implements Serializable {

   private long conversationParticipantId;
   private long conversationId;
   private long userId;

   public ConversationParticipant(
      long conversationParticipantId,
      long conversationId,
      long userId
   ) {
      this.conversationParticipantId = conversationParticipantId;
      this.conversationId = conversationId;
      this.userId = userId;
   }

   public ConversationParticipant(long conversationId, long userId) {
      this.conversationId = conversationId;
      this.userId = userId;
   }

   public long getConversationParticipantId() {
      return conversationParticipantId;
   }

   public void setConversationParticipantId(long conversationParticipantId) {
      this.conversationParticipantId = conversationParticipantId;
   }

   public long getConversationId() {
      return conversationId;
   }

   public void setConversationId(long conversationId) {
      this.conversationId = conversationId;
   }

   public long getUserId() {
      return userId;
   }

   public void setUserId(long userId) {
      this.userId = userId;
   }
}
