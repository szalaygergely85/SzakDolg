package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        editEmail = findViewById(R.id.edtRegEmail);
        editPass= findViewById(R.id.edtRegPass1);
       editPass2 = findViewById(R.id.edtRegPass2);

        btnReg = findViewById(R.id.btnRegReg);


        fireBaseCon = new FireBaseCon();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // String email = edtEmail.getText().toString();
                Toast.makeText(RegisterActivity.this, "email", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO Error kezeles ide is


    /*
    public void onClickRegister(View view) {
        String email = edtEmail.getText();
        String pass = edtPass.getText().toString();
        String pass2 = edtPass2.getText().toString();
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        if (email!=null&&pass!=null && pass==pass2) {
            fireBaseCon.registerNewUser(email, pass.toString());
        }
            if(fireBaseCon.isUserSigned()) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }


    }*/
}