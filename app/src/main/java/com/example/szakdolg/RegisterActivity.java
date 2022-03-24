package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;

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
        firebaseConnect = new FirebaseConnect(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
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
                                Log.d(TAG, "onClick: " + email + "registration process is started");
                                Map<String, String> user = new HashMap<>();
                                user.put("email", email);
                                user.put("pass", pass);
                                user.put("phone", phone);
                                user.put("name", name);
                                RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(RegisterActivity.this);
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
        private Activity a;

        public RegisterAsyncTask(Activity a) {
            this.a = a;
            FirebaseConnect fb = new FirebaseConnect(a);
        }


        @Override
        protected Void doInBackground(Map<String, String>... maps) {
            boolean done = false;
            int tried = 0;

            firebaseConnect.registerNewUser(maps[0]);

            while (!done && tried < 20) {
                SystemClock.sleep(500);
                if (firebaseConnect.isUserSigned()) {
                    done = firebaseConnect.isUserCreated(firebaseConnect.getUserId());
                }
                tried++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (firebaseConnect.isUserSigned()) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

}