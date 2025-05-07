package com.zen_vy.chat.notification;

public class Notification {

   private Long notificationId;
   private String content;
   private Long conversationId;
   private boolean isActive;
   private Long timeStamp;
   private String title;
   private Long userId;

   public Long getNotificationId() {
      return notificationId;
   }

   public void setNotificationId(Long notificationId) {
      this.notificationId = notificationId;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public Long getConversationId() {
      return conversationId;
   }

   public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
   }

   public boolean isActive() {
      return isActive;
   }

   public void setActive(boolean active) {
      isActive = active;
   }

   public Long getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(Long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public Notification(
      Long notificationId,
      String content,
      Long conversationId,
      boolean isActive,
      Long timeStamp,
      String title,
      Long userId
   ) {
      this.notificationId = notificationId;
      this.content = content;
      this.conversationId = conversationId;
      this.isActive = isActive;
      this.timeStamp = timeStamp;
      this.title = title;
      this.userId = userId;
   }
}
