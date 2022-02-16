package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DataBaseConnector dataBaseConnector = new DataBaseConnector();

      //  dataBaseConnector.logoutUser();
       // dataBaseConnector.loginUser("szalaygergely@gmail.com", "mmnvjt");



        if (dataBaseConnector.isUserSigned()){

            // dataBaseConnector.downloadMessages();
             //dataBaseConnector.downloadContacts();


            Intent intent = new Intent(MainActivity.this, MessageBoardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User signed", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


    }
}