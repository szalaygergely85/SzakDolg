package com.zen_vy.chat.models.user.repository;

import com.zen_vy.chat.DTO.LoginRequest;
import com.zen_vy.chat.models.user.entity.User;

import java.io.IOException;
import java.util.List;
import retrofit2.Callback;

public interface UserRepository {
   void getUserByID(Long userId, User currentUser, Callback<User> callback);

   void getUserByToken(String token, Callback<User> callback);

   void deleteUser(Long userId, String token, Callback<Void> callback);

   void deleteUser(String email, String token, Callback<Void> callback);

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

   void forgotPassword(String email, Callback<Void> callback) throws IOException;
}
