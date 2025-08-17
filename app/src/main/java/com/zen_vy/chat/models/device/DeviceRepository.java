package com.zen_vy.chat.models.device;

import retrofit2.Callback;

public interface DeviceRepository {
   void addDevice(Device device, Callback<Device> callback);
}
