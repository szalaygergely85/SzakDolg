package com.zen_vy.chat.models.message.entity;

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
   private String content;

   @Expose
   private boolean encrypted;

   @Expose
   private int type;

   @Expose
   private String uuid;

   private boolean isUploaded;

   private MessageStatus messageStatus;

   public MessageStatus getMessageStatus() {
      return messageStatus;
   }

   public void setMessageStatus(MessageStatus messageStatus) {
      this.messageStatus = messageStatus;
   }

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

   public boolean isUploaded() {
      return isUploaded;
   }

   public void setUploaded(boolean uploaded) {
      isUploaded = uploaded;
   }

   public boolean isEncrypted() {
      return encrypted;
   }

   public void setEncrypted(boolean encrypted) {
      this.encrypted = encrypted;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public MessageEntry() {}

   public MessageEntry(
      Long conversationId,
      Long senderId,
      Long timestamp,
      String content,
      boolean encrypted,
      int type,
      String uuid
   ) {
      this(
         null,
         conversationId,
         senderId,
         timestamp,
         type,
         content,
         encrypted,
         uuid
      );
   }

   public MessageEntry(
      Long messageId,
      Long conversationId,
      Long senderId,
      Long timestamp,

      int type,
      String content,
      boolean encrypted,
      String uuid
   ) {
      this(
         messageId,
         conversationId,
         senderId,
         timestamp,

         type,
         content,
         encrypted,
         uuid,
         false
      );
   }

   public MessageEntry(
      Long messageId,
      Long conversationId,
      Long senderId,
      Long timestamp,

      int type,
      String content,
      boolean encrypted,
      String uuid,
      boolean isUploaded
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;

      this.type = type;
      this.content = content;
      this.encrypted = encrypted;
      this.uuid = uuid;
      this.isUploaded = isUploaded;
   }

   public String getJSON() {
      Gson gson = new GsonBuilder()
         .excludeFieldsWithoutExposeAnnotation()
         .create();
      return gson.toJson(this);
   }
}
