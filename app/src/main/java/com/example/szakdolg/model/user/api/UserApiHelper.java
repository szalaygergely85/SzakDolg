package com.example.szakdolg.model.user.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.entity.UserToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.function.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserApiHelper {

   private UserApiService _userApiService = RetrofitClient
      .getRetrofitInstance()
      .create(UserApiService.class);
   private final String _TAG = "UserApiHelper";

   public void addUser(
      Context context,
      User user,
      Consumer<UserToken> onSuccess
   ) {
      Call<UserToken> call = _userApiService.createUser(user);
      call.enqueue(
         new Callback<UserToken>() {
            @Override
            public void onResponse(
               Call<UserToken> call,
               Response<UserToken> response
            ) {
               if (response.isSuccessful()) {
                  UserToken userToken = response.body();
                  if (userToken != null) {
                     onSuccess.accept(userToken);
                  }
               } else {
                  if (response.code() == HttpURLConnection.HTTP_CONFLICT) {
                     Toast
                        .makeText(
                           context,
                           "Email already registered",
                           Toast.LENGTH_SHORT
                        )
                        .show();
                  } else {
                     Log.e(_TAG, "" + response.code());
                  }
               }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {}
         }
      );
   }

   public void getUserByUserID(
      Long userId,
      String token,
      Consumer<User> onSuccess
   ) {
      Call<User> call = _userApiService.getUserByID(userId, token);
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

   public void getUserByToken(
      Context context,
      Consumer<User> onSuccess,
      String token
   ) {
      Call<User> call = _userApiService.getUserByID(token);
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

   public void getTokenByPasswordAndEmail(
      Context context,
      String hashPassword,
      String email,
      Consumer<UserToken> onSuccess
   ) {
      Call<UserToken> call = _userApiService.logInUser(
         new LoginRequest(email, hashPassword)
      );

      call.enqueue(
         new Callback<UserToken>() {
            @Override
            public void onResponse(
               Call<UserToken> call,
               Response<UserToken> response
            ) {
               Log.d(AppConstants.LOG_TAG, _TAG + response.code());

               if (response.isSuccessful()) {
                  UserToken userToken = response.body();

                  if (userToken != null) {
                     onSuccess.accept(userToken);
                  }
               } else {
                  Log.e(
                     AppConstants.LOG_TAG,
                     _TAG + response.code() + response.message()
                  );
               }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
               Log.e(AppConstants.LOG_TAG, _TAG + t.getMessage());
            }
         }
      );
   }

   @Deprecated
   public void getAndSaveAESKey(User user, String token, Context context) {
      Call<ResponseBody> call = _userApiService.getAESByToken(token);
      call.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {
               if (response.isSuccessful()) {
                  Log.e(_TAG, "server contacted and has file");

                  boolean writtenToDisk = false;
                  try {
                     writtenToDisk =
                     _writeResponseBodyToDisk(response.body(), context, "aes");
                  } catch (IOException e) {
                     throw new RuntimeException(e);
                  }

                  Log.e(_TAG, "file download was a success? " + writtenToDisk);

                  if (writtenToDisk) {
                     try {} catch (Exception e) {
                        throw new RuntimeException(e);
                     }
                  }
               } else {
                  Log.e(_TAG, "server contact failed");
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
         }
      );
   }

   /*
@Deprecated
public void getAndSavePrivateKey(User user, String token, Context context) {
	Call<ResponseBody> call = _userApiService.getKeyByToken(token);
	call.enqueue(
		new Callback<ResponseBody>() {
			@Override
			public void onResponse(
			Call<ResponseBody> call,
			Response<ResponseBody> response
			) {
			if (response.isSuccessful()) {
				Log.e(_TAG, "server contacted and has file");

				boolean writtenToDisk = false;
				try {
					writtenToDisk =
					_writeResponseBodyToDisk(response.body(), context, "key");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				Log.e(_TAG, "file download was a success? " + writtenToDisk);
				if (writtenToDisk) {
					getAndSaveAESKey(user, token, context);
				}
			} else {
				Log.e(_TAG, "server contact failed");
			}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {}
		}
	);
}
*/
   @Deprecated
   public void getAndSavePublicKey(User user, String token, Runnable runnable) {
      Call<String> messagesCall = _userApiService.getPublicKeyByUserId(
         user.getUserId(),
         token
      );

      messagesCall.enqueue(
         new Callback<String>() {
            @Override
            public void onResponse(
               Call<String> call,
               Response<String> response
            ) {
               if (response.isSuccessful()) {
                  String publicKey = response.body();
                  try {
                     if (runnable != null) {
                        runnable.run();
                     }
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
         }
      );
   }

   @Deprecated
   private boolean _writeResponseBodyToDisk(
      ResponseBody body,
      Context c,
      String fileName
   ) throws IOException {
      try {
         File path = new File(c.getFilesDir() + "/key/");
         if (!path.exists()) {
            path.mkdir();
         }

         File futureFile = new File(path + File.separator + fileName);

         InputStream inputStream = null;
         OutputStream outputStream = null;

         try {
            byte[] fileReader = new byte[4096];

            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureFile);

            while (true) {
               int read = inputStream.read(fileReader);

               if (read == -1) {
                  break;
               }

               outputStream.write(fileReader, 0, read);

               fileSizeDownloaded += read;

               Log.e(
                  _TAG,
                  "file download: " + fileSizeDownloaded + " of " + fileSize
               );
            }

            outputStream.flush();

            return true;
         } catch (IOException e) {
            return false;
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }

            if (outputStream != null) {
               outputStream.close();
            }
         }
      } catch (IOException e) {
         return false;
      }
   }
}
