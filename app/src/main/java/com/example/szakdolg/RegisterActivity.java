package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    //TODO more field
    private EditText editEmail;
    private EditText editPass;
    private EditText editPass2;
    private EditText editName;
    private EditText editPhone;
    private Button btnReg;
    private FirebaseConnect firebaseConnect;

    public void initView(){
        editEmail = findViewById(R.id.edtRegEmail);
        editPass= findViewById(R.id.edtRegPass1);
        editPass2 = findViewById(R.id.edtRegPass2);
        editName = findViewById(R.id.edtRegName);
        editPhone = findViewById(R.id.edtRegPhone);
        btnReg = findViewById(R.id.btnRegReg);
        firebaseConnect = new FirebaseConnect();

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
                String phone = editPhone.getText().toString();
                String name = editName.getText().toString();
                Log.d("test2", email);
                Log.d("test", pass);


                if (email!=null && pass.equals(pass2)) {
                    Toast.makeText(RegisterActivity.this, "yooooo", Toast.LENGTH_SHORT).show();

                    Map<String, String> user = new HashMap<>();
                    user.put("email", email);
                    user.put("pass", pass);
                    user.put("phone", phone);
                    user.put("name", name);
                    firebaseConnect.registerNewUser(user);
                    if(firebaseConnect.isUserSigned()) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "nem yoo", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //TODO Error kezeles ide is


}