package com.example.szakdolg.models.user.api;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.entity.UserToken;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {
   @GET("user/id/{userId}")
   Call<User> getUserByID(
      @Path("userId") Long userId,
      @Header("Authorization") String token
   );

   @GET("user/token/{token}")
   Call<User> getUserByID(@Path("token") String token);

   @POST("user/login")
   Call<UserToken> logInUser(@Body LoginRequest loginRequest);

   @POST("user")
   Call<UserToken> createUser(@Body User user);

   @GET("user/publickey/{userId}")
   Call<String> getPublicKeyByUserId(
      @Path("userId") Long userId,
      @Header("Authorization") String token
   );

   @Deprecated
   @GET("user/aes/{token}")
   Call<ResponseBody> getAESByToken(@Path("token") String token);
}
