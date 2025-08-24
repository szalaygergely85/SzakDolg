package com.zen_vy.chat.models.keys;

public class Key {

   long userId;

   String key;

   public Key(long userId, String key) {
      this.userId = userId;
      this.key = key;
   }

   public long getUserId() {
      return userId;
   }

   public void setUserId(long userId) {
      this.userId = userId;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }
}
