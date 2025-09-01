package com.zen_vy.chat.models.message.entity;

public class MessageStatus {

   private Long messageStatusId;
   private String uuid;

   private MessageStatusType messageStatusType;

   public MessageStatus(
      Long messageStatusId,
      String uuid,
      MessageStatusType messageStatusType
   ) {
      this.messageStatusId = messageStatusId;
      this.uuid = uuid;

      this.messageStatusType = messageStatusType;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public MessageStatusType getMessageStatusType() {
      return messageStatusType;
   }

   public void setMessageStatusType(MessageStatusType messageStatusType) {
      this.messageStatusType = messageStatusType;
   }

   public Long getMessageStatusId() {
      return messageStatusId;
   }

   public void setMessageStatusId(Long messageStatusId) {
      this.messageStatusId = messageStatusId;
   }

   public MessageStatus() {}
}
