package com.example.szakdolg.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.activity.MainActivity;
import com.example.szakdolg.activity.MessageBoardActivity;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserApiHelper {
    private final String TAG = "UserApiHelper";
    UserApiService userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);

    public void getAndSavePublicKey(User user, Runnable runnable) {
        Call<String> messagesCall= userApiService.getPublicKeyByUserId(user.getUserId());

        messagesCall.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                   String publicKey= response.body();
                    try {
                       // KeyStoreUtil.savePublicKey(publicKey, user.getEmail());
                        if(runnable!=null){
                            runnable.run();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    public void registerUser(Context context, User user) {
        Call<UserToken> call = userApiService.createUser(user);
        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {

                Log.e(TAG, "" + response.code());
                if (response.isSuccessful()) {
                    UserToken userToken = response.body();
                    if (userToken != null) {
                        _getAndSavePrivateKey(user, userToken.getToken());
                        loginUser(context, user.getPassword(), user.getEmail());
                    }
                } else {
                    Log.e(TAG, "" + response.code());

                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {

            }
        });

    }

    public void getAndSavePrivateKey(User user, String token, Context context) {
        Call<ResponseBody> call = userApiService.getKeyByToken(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "server contacted and has file");

                    boolean writtenToDisk = false;
                    try {
                        writtenToDisk = _writeResponseBodyToDisk(response.body(), context, "key");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.e(TAG, "file download was a success? " + writtenToDisk);
                    if(writtenToDisk){
                        getAndSaveAESKey(user, token, context);
                    }
                } else {
                    Log.e(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void getAndSaveAESKey(User user, String token, Context context) {
        Call<ResponseBody> call = userApiService.getAESByToken(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "server contacted and has file");

                    boolean writtenToDisk = false;
                    try {
                        writtenToDisk = _writeResponseBodyToDisk(response.body(), context, "aes");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.e(TAG, "file download was a success? " + writtenToDisk);

                    if(writtenToDisk){
                        try {
                            //KeyStoreUtil.savePrivateKeyFromFile(context, user.getEmail());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    Log.e(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private boolean _writeResponseBodyToDisk(ResponseBody body, Context c, String fileName) throws IOException {
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

                    Log.e(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
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


    public void getUserByTokenAndNavigateToActivity(Context context, String token) {
        Call<User> call = userApiService.getUser(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG, "" + response.code());
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null) {
                        Intent intent = new Intent(context, MessageBoardActivity.class);
                        intent.putExtra(SharedPreferencesConstans.CURRENT_USER, user);
                        Toast.makeText(context, "A user signed in", Toast.LENGTH_SHORT).show();
                        context.startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "" + response.code());
                    //TODO Handle the error
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());
            }
        });
    }


    public void loginUser(Context context, String hashPassword, String email) {
        Call<UserToken> call = userApiService.logInUser(new LoginRequest(email, hashPassword));

        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                Log.e(TAG, "" + response.code());

                if (response.isSuccessful()) {
                    UserToken userToken = response.body();

                    if (userToken != null) {
                        String token = userToken.getToken();
                        SharedPreferencesUtil.setStringPreference(context, SharedPreferencesConstans.USERTOKEN, token);

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "" + response.code());
                    //TODO Handle the error
                }
            }
            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());
            }
        });
    }
}
