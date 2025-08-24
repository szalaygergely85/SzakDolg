package com.zen_vy.chat.models.user.repository;

import android.content.Context;
import com.zen_vy.chat.DTO.LoginRequest;
import com.zen_vy.chat.models.user.api.UserApiService;
import com.zen_vy.chat.models.user.db.UserDatabaseUtil;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.retrofit.RetrofitClient;
import com.zen_vy.chat.util.DateTimeUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {

   private final UserApiService _userApiService = RetrofitClient
      .getRetrofitInstance()
      .create(UserApiService.class);

   private final Context context;

   public UserRepositoryImpl(Context context) {
      this.context = context;
   }

   @Override
   public void searchUser(
      String search,
      String token,
      Callback<List<User>> callback
   ) {
      _userApiService.searchUser(search, token).enqueue(callback);
   }

   @Override
   public void patchUser(User user, String token, Callback<User> callback) {
      _userApiService
         .patchUser(user, token)
         .enqueue(
            new Callback<User>() {
               @Override
               public void onResponse(
                  Call<User> call,
                  Response<User> response
               ) {
                  if (response.isSuccessful()) {
                     User user = response.body();
                     if (user != null) {
                        UserDatabaseUtil userDatabaseUtil =
                           new UserDatabaseUtil(context, user);
                        userDatabaseUtil.updateUser(user);
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch contact")
                     );
                  }
               }

               @Override
               public void onFailure(Call<User> call, Throwable throwable) {}
            }
         );
   }

   @Override
   public void getUserByID(
      Long userId,
      User currentUser,
      Callback<User> callback
   ) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      User localUser = userDatabaseUtil.getUserById(userId);
      if (localUser != null) {
         if (DateTimeUtil.daysFromNow(localUser.getLastUpdated()) > 1) {
            _getUserByIDApi(userId, currentUser, callback);
         } else {
            callback.onResponse(null, Response.success(localUser));
         }
      } else {
         _getUserByIDApi(userId, currentUser, callback);
      }
   }

   private void _insertUserToDB(User currentUser, User user) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      userDatabaseUtil.insertUser(user);
   }

   private void _getUserByIDApi(
      Long userId,
      User currentUser,
      Callback<User> callback
   ) {
      _userApiService
         .getUserById(userId, currentUser.getToken())
         .enqueue(
            new Callback<User>() {
               @Override
               public void onResponse(
                  Call<User> call,
                  Response<User> response
               ) {
                  if (response.isSuccessful()) {
                     User user = response.body();
                     if (user == null) {
                        callback.onFailure(
                           call,
                           new Throwable("No user Found")
                        );
                        return;
                     }

                     _insertUserToDB(currentUser, user);

                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch contact")
                     );
                  }
               }

               @Override
               public void onFailure(Call<User> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   @Override
   public void getUserByToken(String token, Callback<User> callback) {
      _userApiService
         .getUserByToken(token)
         .enqueue(
            new Callback<User>() {
               @Override
               public void onResponse(
                  Call<User> call,
                  Response<User> response
               ) {
                  if (response.isSuccessful()) {
                     User user = response.body();
                     if (user != null) {
                        UserDatabaseUtil userDatabaseUtil =
                           new UserDatabaseUtil(context, user);
                        userDatabaseUtil.insertUser(user);
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch contact")
                     );
                  }
               }

               @Override
               public void onFailure(Call<User> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   @Override
   public void deleteUser(Long userId, String token, Callback<Void> callback) {
      _userApiService
         .deleteUser(userId, token)
         .enqueue(
            new Callback<Void>() {
               @Override
               public void onResponse(
                  Call<Void> call,
                  Response<Void> response
               ) {
                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(Call<Void> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to delete User")
                  );
               }
            }
         );
   }

   @Override
   public void deleteUser(String email, String token, Callback<Void> callback) {
      _userApiService
         .deleteUser(email, token)
         .enqueue(
            new Callback<Void>() {
               @Override
               public void onResponse(
                  Call<Void> call,
                  Response<Void> response
               ) {
                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(Call<Void> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to delete User")
                  );
               }
            }
         );
   }

   @Override
   public void getTokenByPasswordAndEmail(
      LoginRequest loginRequest,
      Callback<User> callback
   ) {
      _userApiService
         .getTokenByPasswordAndEmail(loginRequest)
         .enqueue(
            new Callback<User>() {
               @Override
               public void onResponse(
                  Call<User> call,
                  Response<User> response
               ) {
                  if (response.isSuccessful()) {
                     if (response.body() != null) {
                        User user = response.body();

                        UserDatabaseUtil userDatabaseUtil =
                           new UserDatabaseUtil(context, user);
                        userDatabaseUtil.insertUser(user);
                     }
                  }

                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(Call<User> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   @Override
   public void addUser(User user, Callback<User> callback) {
      _userApiService
         .addUser(user)
         .enqueue(
            new Callback<User>() {
               @Override
               public void onResponse(
                  Call<User> call,
                  Response<User> response
               ) {
                  if (response.isSuccessful()) {
                     User userRemote = response.body();
                     if (userRemote != null) {
                        UserDatabaseUtil userDatabaseUtil =
                           new UserDatabaseUtil(context, userRemote);
                        userDatabaseUtil.insertUser(userRemote);
                     }
                  }

                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(Call<User> call, Throwable throwable) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   @Override
   public void getPublicKeyByUserId(
      Long userId,
      String token,
      Callback<String> callback
   ) {}
}
