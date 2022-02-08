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
        FireBaseCon fireBaseCon = new FireBaseCon();


        if (fireBaseCon.isUserSigned()){
            Toast.makeText(this, "User signed", Toast.LENGTH_SHORT).show();
        }else{

        }

        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}