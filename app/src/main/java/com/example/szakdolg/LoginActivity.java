package com.example.szakdolg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editMail;
    private EditText editPass;
    private TextView txtForgot;
    private Button btnReg;
    private Button btnLog;
    private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private static final String TAG = "LoginActivity";

    public void initView() {
        editMail = findViewById(R.id.edtLgnEmail);
        editPass = findViewById(R.id.edtLgnPass);
        btnReg = findViewById(R.id.btnLgnReg);
        btnLog = findViewById(R.id.btnLgnLogin);
        txtForgot = findViewById(R.id.txtLgnForgot);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messenger");
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                String pass = editPass.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()) {
                    firebaseConnect.loginUser(email, pass, LoginActivity.this);
                    firebaseConnect.mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseConnect.mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "onAuthStateChanged: " + firebaseConnect.getUserId());
                                Intent intent = new Intent(LoginActivity.this, MessageBoardActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    Error.GetErrorMessageInToast("e6", LoginActivity.this);
                }
            }
        });
    }
}