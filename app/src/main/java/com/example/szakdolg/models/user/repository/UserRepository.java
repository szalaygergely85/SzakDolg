package com.example.szakdolg.models.user.repository;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;
import retrofit2.Callback;

public interface UserRepository {
   void getUserByID(Long userId, User currentUser, Callback<User> callback);

   void getUserByToken(String token, Callback<User> callback);

   void getTokenByPasswordAndEmail(
      LoginRequest loginRequest,
      Callback<User> callback
   );

   void patchUser(User user, String token, Callback<User> callback);

   void addUser(User user, Callback<User> callback);

   void searchUser(String search, String token, Callback<List<User>> callback);

   void getPublicKeyByUserId(
      Long userId,
      String token,
      Callback<String> callback
   );
}
