package com.example.szakdolg.model.message.entity;

import java.io.Serializable;

public class MessageEntry implements Serializable {

   private Long messageId;

   private Long conversationId;

   private Long senderId;

   private Long timestamp;

   private String contentEncrypted;

   private boolean isRead;

   private int type;

   private String content;

   public MessageEntry() {}

   public Long getMessageId() {
      return messageId;
   }

   public void setMessageId(Long messageId) {
      this.messageId = messageId;
   }

   public Long getConversationId() {
      return conversationId;
   }

   public void setConversationId(long conversationId) {
      this.conversationId = conversationId;
   }

   public Long getSenderId() {
      return senderId;
   }

   public void setSenderId(long senderId) {
      this.senderId = senderId;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
   }

   public String getContentEncrypted() {
      return contentEncrypted;
   }

   public void setContentEncrypted(String contentEncrypted) {
      this.contentEncrypted = contentEncrypted;
   }

   public boolean isRead() {
      return isRead;
   }

   public void setRead(boolean read) {
      isRead = read;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   @Deprecated
   public MessageEntry(
      long conversationId,
      long senderId,
      long timestamp,
      String contentEncrypted,
      String uUId
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
   }

   @Deprecated
   public MessageEntry(
      Long messageId,
      long conversationId,
      long senderId,
      long timestamp,
      String contentEncrypted,
      String uUId
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
   }

   public MessageEntry(
      Long messageId,
      Long conversationId,
      Long senderId,
      Long timestamp,
      String contentEncrypted,
      boolean isRead,
      int type,
      String content
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
      this.isRead = isRead;
      this.type = type;
      this.content = content;
   }

   public MessageEntry(
      long conversationId,
      long senderId,
      long timestamp,
      String contentEncrypted,
      int type,
      String content,
      String uUId
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
      this.type = type;
      this.content = content;
   }

   public MessageEntry(
      long conversationId,
      long senderId,
      String contentEncrypted,
      int type,
      String content
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = System.currentTimeMillis();
      this.contentEncrypted = contentEncrypted;
      this.type = type;
      this.content = content;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   @Override
   public String toString() {
      return (
         "MessageEntry{" +
         "messageId=" +
         messageId +
         ", conversationId=" +
         conversationId +
         ", senderId=" +
         senderId +
         ", timestamp=" +
         timestamp +
         ", content='" +
         contentEncrypted +
         '\'' +
         ", isRead=" +
         isRead +
         ", type=" +
         type +
         ", contentSenderVersion='" +
         content +
         '\'' +
         '}'
      );
   }
}
