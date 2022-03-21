package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseConnect firebaseConnect = new FirebaseConnect(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseConnect.logoutUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseConnect.isUserSigned()) {
            Intent intent = new Intent(MainActivity.this, MessageBoardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User signed", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}