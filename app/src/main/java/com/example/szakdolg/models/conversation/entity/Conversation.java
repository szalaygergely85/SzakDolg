package com.example.szakdolg.models.conversation.entity;

import java.io.Serializable;
import java.util.Objects;

public class Conversation implements Serializable {

   private Long conversationId;
   private String conversationName;
   private long timeStamp;
   private long creatorUserId;
   private int numberOfParticipants;

   public Conversation() {}

   public Conversation(
      Long conversationId,
      String conversationName,
      long timeStamp,
      Long creatorUserId,
      int numberOfParticipants
   ) {
      this.conversationId = conversationId;
      this.conversationName = conversationName;
      this.timeStamp = timeStamp;
      this.creatorUserId = creatorUserId;
      this.numberOfParticipants = numberOfParticipants;
   }

   public Conversation(
      String conversationName,
      long timeStamp,
      Long creatorUserId,
      int numberOfParticipants
   ) {
      this.conversationName = conversationName;
      this.timeStamp = timeStamp;
      this.creatorUserId = creatorUserId;
      this.numberOfParticipants = numberOfParticipants;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true; // Check for reference equality
      if (obj == null || getClass() != obj.getClass()) return false; // Check for null or different class
      Conversation other = (Conversation) obj;
      return conversationId.equals(other.conversationId); // Compare only by ID
   }

   @Override
   public int hashCode() {
      return Objects.hash(conversationId); // Hash based on ID
   }

   public long getConversationId() {
      return conversationId;
   }

   public String getConversationName() {
      return conversationName;
   }

   public long getTimeStamp() {
      return timeStamp;
   }

   public long getCreatorUserId() {
      return creatorUserId;
   }

   public int getNumberOfParticipants() {
      return numberOfParticipants;
   }

   public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
   }

   public void setConversationName(String conversationName) {
      this.conversationName = conversationName;
   }

   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public void setCreatorUserId(long creatorUserId) {
      this.creatorUserId = creatorUserId;
   }

   public void setNumberOfParticipants(int numberOfParticipants) {
      this.numberOfParticipants = numberOfParticipants;
   }
}
