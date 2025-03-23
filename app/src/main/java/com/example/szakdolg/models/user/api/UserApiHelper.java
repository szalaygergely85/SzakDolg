package com.example.szakdolg.models.user.api;

import android.util.Log;

import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserApiHelper {

   private final UserApiService _userApiService = RetrofitClient
      .getRetrofitInstance()
      .create(UserApiService.class);
   private final String _TAG = "UserApiHelper";

   public void getUserByUserID(
      Long userId,
      String token,
      Consumer<User> onSuccess
   ) {
      Call<User> call = _userApiService.getUserById(userId, token);
      call.enqueue(
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               Log.e(_TAG, "" + response.code());
               if (response.isSuccessful()) {
                  User user = response.body();
                  if (user != null) {
                     onSuccess.accept(user);
                  }
               } else {
                  Log.e(_TAG, +response.code() + " " + response.errorBody());
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
               Log.e(_TAG, "" + t.getMessage());
            }
         }
      );
   }

}
