package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText editMail;
    private EditText editPass;
    private Button btnReg;
    private FireBaseCon fb;
    public void initView(){
        editMail = findViewById(R.id.edtLgnEmail);
        editPass = findViewById(R.id.edtLgnPass);
        btnReg = findViewById(R.id.btnLgnReg);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fb = new FireBaseCon();
        initView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //TODO Hiba kezeles
    public void onClickLogin(View view){

        try {
            fb.loginUser(editMail.getText().toString(), editPass.getText().toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

        }catch (Exception e){
            Log.d("FireBase", "Ajajajaj");
        }
    }


}