package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseConnect firebaseConnect = new FirebaseConnect(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: "+ Error.getErrorMessage("e1", this));
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