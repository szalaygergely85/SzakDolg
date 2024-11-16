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

   private String uuid;

   private boolean isUploaded;


   public Long getMessageId() {
      return messageId;
   }

   public void setMessageId(Long messageId) {
      this.messageId = messageId;
   }

   public Long getConversationId() {
      return conversationId;
   }

   public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
   }

   public Long getSenderId() {
      return senderId;
   }

   public void setSenderId(Long senderId) {
      this.senderId = senderId;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
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

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getUuId() {
      return uuid;
   }

   public void setUuId(String uuid) {
      this.uuid = uuid;
   }

   public boolean isUploaded() {
      return isUploaded;
   }

   public void setUploaded(boolean uploaded) {
      isUploaded = uploaded;
   }

   public MessageEntry() {}

   public MessageEntry(
      Long messageId,
      Long conversationId,
      Long senderId,
      Long timestamp,
      String contentEncrypted,
      boolean isRead,
      int type,
      String content,
      String uuid
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
      this.isRead = isRead;
      this.type = type;
      this.content = content;
      this.uuid = uuid;
   }
   public MessageEntry(
           Long messageId,
           Long conversationId,
           Long senderId,
           Long timestamp,
           String contentEncrypted,
           boolean isRead,
           int type,
           String content,
           String uuid,
           boolean isUploaded
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
      this.isRead = isRead;
      this.type = type;
      this.content = content;
      this.uuid = uuid;
      this.isUploaded = isUploaded;
   }
}
