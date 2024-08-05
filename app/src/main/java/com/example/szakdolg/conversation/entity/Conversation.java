package com.example.szakdolg.conversation.entity;

public class Conversation {

   private long conversationId;
   private String conversationName;
   private long timeStamp;
   private long creatorUserId;
   private int numberOfParticipants;

   public Conversation(
      long conversationId,
      String conversationName,
      long timeStamp,
      long creatorUserId,
      int numberOfParticipants
   ) {
      this.conversationId = conversationId;
      this.conversationName = conversationName;
      this.timeStamp = timeStamp;
      this.creatorUserId = creatorUserId;
      this.numberOfParticipants = numberOfParticipants;
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
}
