package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseConnect firebaseConnect = new FirebaseConnect();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseConnect.logoutUser();
       //firebaseConnect.loginUser("szalaygergely@gmail.com", "mmnvjt");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseConnect.isUserSigned()){
            // dataBaseConnector.downloadMessages();
            //dataBaseConnector.downloadContacts();
            Intent intent = new Intent(MainActivity.this, MessageBoardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User signed", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}