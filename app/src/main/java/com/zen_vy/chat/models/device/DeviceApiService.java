package com.zen_vy.chat.models.device;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DeviceApiService {
   @POST("devices/add-device")
   Call<Device> addDevice(
      @Body Device device,
      @Header("Authorization") String token
   );
}
