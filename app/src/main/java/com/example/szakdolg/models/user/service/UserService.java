package com.example.szakdolg.models.user.service;

import android.content.Context;
import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.repository.UserRepository;
import com.example.szakdolg.models.user.repository.UserRepositoryImpl;

import java.net.HttpURLConnection;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

   private Context context;

   private UserRepository userRepository;

   public UserService(Context context) {
      this.context = context;
      this.userRepository = new UserRepositoryImpl(context);
   }


   public void getTokenByPasswordAndEmail(
      String hashPassword,
      String email,
      final UserService.LoginCallback<User> callback
   ) {
      userRepository.getTokenByPasswordAndEmail(
         new LoginRequest(email, hashPassword),
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                   if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                       callback.onUserNotFound();
                   }else {
                       callback.onError(new Throwable("Failed to login"));
                   }
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void updateUser(
      User user,
      String token,
      final UserService.UserCallback<User> callback
   ) {
      userRepository.patchUser(
         user,
         token,
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {}
         }
      );
   }

   public void getUserByToken(
      String token,
      final UserService.UserCallback<User> callback
   ) {
      userRepository.getUserByToken(
         token,
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void getUserByUserId(
      Long userId,
      User currentUser,
      final UserService.UserCallback<User> callback
   ) {
      userRepository.getUserByID(
         userId,
         currentUser,
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               if (response.isSuccessful() && response.body() != null) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void searchUser(
      String search,
      User user,
      final UserService.UserCallback<List<User>> callback
   ) {
      userRepository.searchUser(
         search,
         user.getToken(),
         new Callback<List<User>>() {
            @Override
            public void onResponse(
               Call<List<User>> call,
               Response<List<User>> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void addUser(
      User user,
      final UserService.LoginCallback<User> callback
   ) {
      userRepository.addUser(
         user,
         new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                   if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                       callback.onUserNotFound();
                   }else {
                       callback.onError(new Throwable("Failed to login"));
                   };
               }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
               callback.onError(throwable);
            }
         }
      );
   }

   public interface UserCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }

    public interface LoginCallback<T> {
        void onSuccess(T data);
        void onUserNotFound();
        void onError(Throwable t);
    }
}
