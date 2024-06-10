package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.user.UserApiHelper;
import com.example.szakdolg.util.ErrorUtil;
import com.example.szakdolg.FirebaseConnect;
import com.example.szakdolg.ForgotPassword;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.UserApiService;
import com.example.szakdolg.user.UserToken;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText editMail;
    private EditText editPass;
    private TextView txtForgot;
    private Button btnReg;
    private Button btnLog;
    private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private static final String TAG = "LoginActivity";
    private UserApiHelper userApiHelper = new UserApiHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _initView();
        _setToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();


        _setOnClickListeners();

    }

    public void _initView() {
        editMail = findViewById(R.id.edtLgnEmail);
        editPass = findViewById(R.id.edtLgnPass);
        btnReg = findViewById(R.id.btnLgnReg);
        btnLog = findViewById(R.id.btnLgnLogin);
        txtForgot = findViewById(R.id.txtLgnForgot);
    }


    private void _setOnClickListeners() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editMail.getText().toString();
                String password = editPass.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    String hashPassword = HashUtils.hashPassword(password);
                    userApiHelper.loginUser(LoginActivity.this, hashPassword, email);
                } else {
                    ErrorUtil.GetErrorMessageInToast("e6", LoginActivity.this);
                }
            }
        });

    }

    private void _setToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messenger");
    }
}