package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    //TODO more field
    private EditText editEmail;
    private EditText editPass;
    private EditText editPass2;
    private Button btnReg;
    private FireBaseCon fireBaseCon;

    public void initView(){
        editEmail = findViewById(R.id.edtRegEmail);
        editPass= findViewById(R.id.edtRegPass1);
        editPass2 = findViewById(R.id.edtRegPass2);
        btnReg = findViewById(R.id.btnRegReg);
        fireBaseCon = new FireBaseCon();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }


    protected void onStart(){
        super.onStart();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String pass = editPass.getText().toString();
                String pass2 = editPass2.getText().toString();
                Log.d("test2", email);
                Log.d("test", pass);


                if (email!=null && pass.equals(pass2)) {
                    Toast.makeText(RegisterActivity.this, "yooooo", Toast.LENGTH_SHORT).show();
                    Log.e("test2", email);
                    Log.e("test", pass);
                    fireBaseCon.registerNewUser(email, pass);
                }else{
                    Toast.makeText(RegisterActivity.this, "nem yoo", Toast.LENGTH_SHORT).show();
                }
                if(fireBaseCon.isUserSigned()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    //TODO Error kezeles ide is


}