package com.zen_vy.chat.models.device;

import android.content.Context;
import com.zen_vy.chat.models.user.entity.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceService {

   private Context context;
   private User currentUser;

   DeviceRepository deviceRepository;

   public DeviceService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.deviceRepository = new DeviceRepositoryImpl(context, currentUser);
   }

   public interface DeviceCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }

   public void addDevice(
      Device device,
      final DeviceService.DeviceCallback<Device> callback
   ) {
      deviceRepository.addDevice(
         device,
         new Callback<Device>() {
            @Override
            public void onResponse(
               Call<Device> call,
               Response<Device> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }
}
