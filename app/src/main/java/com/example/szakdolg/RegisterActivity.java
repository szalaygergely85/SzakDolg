package com.example.szakdolg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
    private FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private static final String TAG = "RegisterActivity";

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

    public void initView() {
        editEmail = findViewById(R.id.edtRegEmail);
        editPass = findViewById(R.id.edtRegPass1);
        editPass2 = findViewById(R.id.edtRegPass2);
        editName = findViewById(R.id.edtRegName);
        editPhone = findViewById(R.id.edtRegPhone);
        btnReg = findViewById(R.id.btnRegReg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(mToolbar);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Register");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    protected void onStart() {
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
                                Log.d(TAG, "onClick: " + email + " registration process is started");
                                Map<String, String> user = new HashMap<>();
                                user.put("email", email);
                                user.put("pass", pass);
                                user.put("phone", phone);
                                user.put("name", name);
                                RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
                                registerAsyncTask.execute(user);
                            } else {
                                Error.GetErrorMessageInToast("e2", RegisterActivity.this);
                            }
                        } else {
                            Error.GetErrorMessageInToast("e3", RegisterActivity.this);
                        }
                    } else {
                        Error.GetErrorMessageInToast("e4", RegisterActivity.this);
                    }
                } else {
                    Error.GetErrorMessageInToast("e5", RegisterActivity.this);
                }


            }
        });

    }
    //TODO Error kezeles ide is

    public class RegisterAsyncTask extends AsyncTask<Map<String, String>, Void, Void> {


        @Override
        protected Void doInBackground(Map<String, String>... maps) {

            firebaseConnect.registerNewUser(maps[0], RegisterActivity.this);
            firebaseConnect.mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener(){
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseConnect.isUserSigned()) {
                        Log.d(TAG, "onAuthStateChanged: " + firebaseConnect.getUserId() + " user is signed");

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);

                    }
                }
            });
            return null;
        }

    }

}