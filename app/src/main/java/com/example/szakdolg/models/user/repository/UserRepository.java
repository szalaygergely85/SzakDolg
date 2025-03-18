package com.example.szakdolg.models.user.repository;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.entity.UserToken;

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.Path;


public interface UserRepository {


    void getUserByID(
            Long userId,
            String token,
            Callback<User> callback
    );

    void getUserByToken(String token, Callback<User> callback);

    void getTokenByPasswordAndEmail(LoginRequest loginRequest, Callback<UserToken> callback);

    void addUser(User user, Callback<User> callback);

    void searchUser(String search, String token, Callback <List<User>> callback);

    void getPublicKeyByUserId(
            Long userId,
            String token, Callback<String> callback
    );
}
