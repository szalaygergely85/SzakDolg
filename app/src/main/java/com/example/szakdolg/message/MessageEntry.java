package com.example.szakdolg.message;

import java.io.Serializable;

public class MessageEntry implements Serializable {

   private Long messageId;

   private long conversationId;

   private long senderId;

   private long timestamp;

   private String content;

   private boolean isRead;

   private int type;

   private String contentSenderVersion;

   public Long getMessageId() {
      return messageId;
   }

   public void setMessageId(Long messageId) {
      this.messageId = messageId;
   }

   public long getConversationId() {
      return conversationId;
   }

   public void setConversationId(long conversationId) {
      this.conversationId = conversationId;
   }

   public long getSenderId() {
      return senderId;
   }

   public void setSenderId(long senderId) {
      this.senderId = senderId;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
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

   public MessageEntry(
      long conversationId,
      long senderId,
      long timestamp,
      String content
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.content = content;
   }

   public MessageEntry(
      Long messageId,
      long conversationId,
      long senderId,
      long timestamp,
      String content
   ) {
      this.messageId = messageId;
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.content = content;
   }

   public MessageEntry(
      long conversationId,
      long senderId,
      long timestamp,
      String content,
      int type,
      String contentSenderVersion
   ) {
      this.conversationId = conversationId;
      this.senderId = senderId;
      this.timestamp = timestamp;
      this.content = content;
      this.type = type;
      this.contentSenderVersion = contentSenderVersion;
   }

   public String getContentSenderVersion() {
      return contentSenderVersion;
   }

   public void setContentSenderVersion(String contentSenderVersion) {
      this.contentSenderVersion = contentSenderVersion;
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
         content +
         '\'' +
         '}'
      );
   }
}
