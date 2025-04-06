package com.example.szakdolg.models.message.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class MessageEntry implements Serializable {

   private Long messageId;

   @Expose
   private Long conversationId;

   @Expose
   private Long senderId;

   @Expose
   private Long timestamp;

   @Expose
   private String contentEncrypted;

   private boolean isRead;

   @Expose
   private int type;

   @Expose
   private String content;

   @Expose
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

   public MessageEntry(
      Long conversationId,
      Long senderId,
      Long timestamp,
      String contentEncrypted,
      int type,
      String uuid
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.contentEncrypted = contentEncrypted;
      this.isRead = false;
      this.type = type;
      this.content = null;
      this.uuid = uuid;
      this.isUploaded = true;
   }

   public String getJSON() {
      Gson gson = new GsonBuilder()
         .excludeFieldsWithoutExposeAnnotation()
         .create();
      return gson.toJson(this);
   }
}
