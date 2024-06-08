package com.example.szakdolg.user;

import com.example.szakdolg.DTO.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {

    @GET("user/id/{id}")
    Call<User> getAllUser(
            @Path("id") long userId);

    @GET("user/token/{token}")
    Call<User> getUser(
            @Path("token") String token);

    @POST("user/login")
    Call<UserToken> logInUser(
            @Body LoginRequest loginRequest);

    @POST("user")
    Call<UserToken> createUser(
            @Body User user);
}
