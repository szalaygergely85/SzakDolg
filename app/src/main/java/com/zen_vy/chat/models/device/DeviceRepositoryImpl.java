package com.zen_vy.chat.models.device;

import android.content.Context;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRepositoryImpl implements DeviceRepository {

   private final Context context;
   private final User currentUser;

   DeviceApiService deviceApiService;

   public DeviceRepositoryImpl(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.deviceApiService =
      RetrofitClient.getRetrofitInstance().create(DeviceApiService.class);
   }

   @Override
   public void addDevice(Device device, Callback<Device> callback) {
      deviceApiService
         .addDevice(device, currentUser.getToken())
         .enqueue(
            new Callback<Device>() {
               @Override
               public void onResponse(
                  Call<Device> call,
                  Response<Device> response
               ) {
                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(Call<Device> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }
}
