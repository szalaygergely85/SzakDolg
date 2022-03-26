package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassword extends AppCompatActivity {
    private EditText email;
    private Button send;
    FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");

    public void initView() {
        email = findViewById(R.id.edtFrgtEmail);
        send = findViewById(R.id.btnFrgtSend);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseConnect.sendForgotPassword(email.getText().toString());
            }
        });
    }
}