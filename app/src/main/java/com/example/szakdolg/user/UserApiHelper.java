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
                        KeyStoreUtil.savePublicKey(publicKey, user.getEmail());
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

    private void _getAndSavePrivateKey(User user, String token) {
        Call<String> call = userApiService.getKeyByToken(token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "" + response.code());
                if (response.isSuccessful()) {
                    String privateKey = response.body();
                    try {
                        KeyStoreUtil.savePrivateKey(privateKey, user.getEmail());
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
                        intent.putExtra(SharedPreferencesConstans.LOGGED_USER, user);
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
