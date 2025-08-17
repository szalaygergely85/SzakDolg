package com.zen_vy.chat.models.device;

public class Device {

   private long deviceId;

   private long userId;

   private String deviceToken;

   public Device() {}

   public Device(long userId, String deviceToken) {
      this.userId = userId;
      this.deviceToken = deviceToken;
   }

   public long getDeviceId() {
      return deviceId;
   }

   public void setDeviceId(long deviceId) {
      this.deviceId = deviceId;
   }

   public long getUserId() {
      return userId;
   }

   public void setUserId(long userId) {
      this.userId = userId;
   }

   public String getDeviceToken() {
      return deviceToken;
   }

   public void setDeviceToken(String deviceToken) {
      this.deviceToken = deviceToken;
   }
}
