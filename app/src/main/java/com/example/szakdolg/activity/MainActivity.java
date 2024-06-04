package com.example.szakdolg.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.szakdolg.FirebaseConnect;
import com.example.szakdolg.R;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserApiService;
import com.example.szakdolg.util.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private static final int READ_PERMISSION_CODE = 202;
    private static final int WRITE_PERMISSION_CODE = 203;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String token = SharedPreferencesUtil.getStringPreference(this, "auth_token");

        if (token != null) {

            UserApiService userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);

            Call<User> call = userApiService.getUser(token);
            call.enqueue(new Callback<User>(){
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    Log.e(TAG, ""+response.code());

                    if (response.isSuccessful()) {
                        User user = response.body();

                        if(user !=null){

                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                            intent.putExtra("user", user);

                            startActivity(intent);

                            Toast.makeText(MainActivity.this, "A user signed in", Toast.LENGTH_SHORT).show();

                            finish();
                        }


                    } else {
                        Log.e(TAG, ""+response.code());
                        //TODO Handle the error
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, ""+t.getMessage());
                }
            });

        } else {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        /*

        UserApiService userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);

        Call<User> call = userApiService.getAllUser(1);

        call.enqueue(new Callback<User>(){
                         @Override
                         public void onResponse(Call<User> call, Response<User> response) {
                             Log.e(TAG, ""+response.code());

                             if (response.isSuccessful()) {
                                 User user = response.body();
                                 Log.i(TAG, user.getEmail());
                                 // Handle the user object
                             } else {
                                 Log.e(TAG, ""+response.code());
                                 // Handle the error
                             }
                         }

                         @Override
                         public void onFailure(Call<User> call, Throwable t) {
                             Log.e(TAG, ""+t.getMessage());
                         }
                     }

        );

        if (firebaseConnect.isUserSigned()) {
            Intent intent = new Intent(MainActivity.this, MessageBoardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "A user signed in", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

         */
    }
}