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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPass;
    private EditText editPass2;
    private EditText editName;
    private EditText editPhone;
    private Button btnReg;
    private FirebaseConnect firebaseConnect;

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void initView(){
        editEmail = findViewById(R.id.edtRegEmail);
        editPass= findViewById(R.id.edtRegPass1);
        editPass2 = findViewById(R.id.edtRegPass2);
        editName = findViewById(R.id.edtRegName);
        editPhone = findViewById(R.id.edtRegPhone);
        btnReg = findViewById(R.id.btnRegReg);
        firebaseConnect = new FirebaseConnect(this);
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
              

                    if (!email.isEmpty() && !pass2.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !phone.isEmpty()) {
                        if (pass.equals(pass2)) {
                            if (pass.length() > 5) {
                                if (isEmailValid(email)) {
                                        Toast.makeText(RegisterActivity.this, "yooooo", Toast.LENGTH_SHORT).show();
                                        Map<String, String> user = new HashMap<>();
                                        user.put("email", email);
                                        user.put("pass", pass);
                                        user.put("phone", phone);
                                        user.put("name", name);
                                        RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
                                        registerAsyncTask.execute(user);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Email Address is not Valid", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Password must be minimum 6 character long", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Password and repeat password must be the same", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "These fields are mandatory to fill", Toast.LENGTH_LONG).show();
                    }

            }
        });

    }
    //TODO Error kezeles ide is

    public class RegisterAsyncTask extends AsyncTask<Map<String, String>, Void, Void> {
        FirebaseConnect fb = new FirebaseConnect(this);

        @Override
        protected Void doInBackground(Map<String, String>... maps) {
            boolean done = false;
            int tried=0;
            // Log.d("async", maps[0].toString());
            firebaseConnect.registerNewUser(maps[0]);

            while (!done && tried<20) {
                // Log.d("async", "i am indaa loop " + tried);
                SystemClock.sleep(500);
                if (fb.isUserSigned()){
                    done = fb.isUserCreated(fb.getUserId());
                }
                tried++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            // Log.d("async", "post execute");
            if(firebaseConnect.isUserSigned()) {
               Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                // Log.d("async", "User signed");
            }
        }
    }

}