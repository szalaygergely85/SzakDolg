package com.zen_vy.chat.models.user.api;

import com.zen_vy.chat.DTO.LoginRequest;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {
   @GET("user/id/{userId}")
   Call<User> getUserById(
      @Path("userId") Long userId,
      @Header("Authorization") String token
   );

   @GET("user/token/{token}")
   Call<User> getUserByToken(@Path("token") String token);

   @GET("user/search/{search}")
   Call<List<User>> searchUser(
      @Path("search") String search,
      @Header("Authorization") String token
   );

   @POST("user/login")
   Call<User> getTokenByPasswordAndEmail(@Body LoginRequest loginRequest);

   @POST("user")
   Call<User> addUser(@Body User user);

   @PATCH("user")
   Call<User> patchUser(@Body User user, @Header("Authorization") String token);

   @GET("user/publickey/{userId}")
   Call<String> getPublicKeyByUserId(
      @Path("userId") Long userId,
      @Header("Authorization") String token
   );

   @DELETE("user/remove-user/id/{userId}")
   Call<Void> deleteUser(
      @Path("userId") Long userId,
      @Header("Authorization") String token
   );

   @DELETE("user/remove-user/email/{email}")
   Call<Void> deleteUser(
      @Path("email") String email,
      @Header("Authorization") String token
   );
}
