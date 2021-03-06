package com.example.szakdolg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {
    private EditText email;
    private Button send;
    FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");

    public void initView() {
        email = findViewById(R.id.edtFrgtEmail);
        send = findViewById(R.id.btnFrgtSend);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initView();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.frgtToolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Forgot Password");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseConnect.sendForgotPassword(email.getText().toString());
                Toast.makeText(ForgotPassword.this, "Instruction sent to the email address", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}