package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText editMail;
    private EditText editPass;
    private Button btnReg;
    private Button btnLog;
    private DataBaseConnector fb;


    public void initView(){
        editMail = findViewById(R.id.edtLgnEmail);
        editPass = findViewById(R.id.edtLgnPass);
        btnReg = findViewById(R.id.btnLgnReg);
        btnLog = findViewById(R.id.btnLgnLogin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fb = new DataBaseConnector();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO Hiba kezeles
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.loginUser(editMail.getText().toString(), editPass.getText().toString());

                if(fb.isUserSigned()) {
                    Intent intent = new Intent(LoginActivity.this, MessageBoardActivity.class);
                    startActivity(intent);
                }
            }
        });

    }





}