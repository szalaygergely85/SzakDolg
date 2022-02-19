package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
     EditText editMail;
     EditText editPass;
    private Button btnReg;
    private Button btnLog;
    FirebaseConnect fb = new FirebaseConnect();


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
        initView();
        fb.logoutUser();
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
                Map<String, String> user = new HashMap<>();
                user.put("email", editMail.getText().toString());
                user.put("pass", editPass.getText().toString());
                //b.loginUser(editMail.getText().toString(), editPass.getText().toString());
                /*if(fb.isUserSigned()) {
                    Intent intent = new Intent(LoginActivity.this, MessageBoardActivity.class);
                    startActivity(intent);
                }*/

                LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute(user);
            }
        });
    }

    public class LoginAsyncTask extends AsyncTask<Map<String, String>, Void, Void> {
        FirebaseConnect fb = new FirebaseConnect();

        @Override
        protected Void doInBackground(Map<String, String>... maps) {
            boolean done = false;
            int tried=0;
            String email = maps[0].get("email").toString();
            String pass = maps[0].get("pass").toString();
            fb.loginUser(email,pass);
            while (!done && tried<10) {
                Log.d("async", "i am indaa loop " + tried);
                SystemClock.sleep(100);
                done = fb.isUserSigned();
                tried++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.d("async", "post execute");
            if (fb.isUserSigned()) {
                Log.d("async", "signed?");
                Intent intent = new Intent(LoginActivity.this, MessageBoardActivity.class);
                startActivity(intent);
            }
        }
    }




}