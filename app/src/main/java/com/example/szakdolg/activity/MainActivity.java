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

import com.example.szakdolg.DTO.MessageBoard;
import com.example.szakdolg.FirebaseConnect;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserApiHelper;
import com.example.szakdolg.user.UserApiService;
import com.example.szakdolg.util.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int READ_PERMISSION_CODE = 202;
    private static final int WRITE_PERMISSION_CODE = 203;
    String token;
    UserApiHelper userApiHelper= new UserApiHelper();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _setPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();

        token = SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.USERTOKEN);

        if (token != null) {
            userApiHelper.getUserByTokenAndNavigateToActivity(MainActivity.this, token);
        } else {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void _setPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        }
    }
}