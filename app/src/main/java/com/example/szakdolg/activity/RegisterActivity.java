package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolg.user.api.UserApiHelper;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.ErrorUtil;

import com.example.szakdolg.R;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPass;
    private EditText editFullName;
    private EditText editPass2;
    private EditText editDisplayName;
    private EditText editPhone;
    private TextView textSignIn;
    private Button btnReg;

    private static final String TAG = "RegisterActivity";
    UserApiHelper userApiHelper = new UserApiHelper();
    private String email;
    private String pass;
    private String pass2;
    private String phone;
    private String displayName;
    private String fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        _initView();
        _setOnClickListeners();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    protected void onStart() {
        super.onStart();

    }


    private void _initView() {
        editEmail = findViewById(R.id.edtRegEmail);
        editPass = findViewById(R.id.edtRegPass1);
        editPass2 = findViewById(R.id.edtRegPass2);
        editDisplayName = findViewById(R.id.edtRegName);
        editFullName = findViewById(R.id.edtFullName);
        editPhone = findViewById(R.id.edtRegPhone);
        btnReg = findViewById(R.id.btnRegReg);
        textSignIn = findViewById(R.id.textSignIn);
    }

    public static boolean _isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean _isFieldNotEmpty(String email, String pass, String pass2, String displayName, String fullName, String phone) {
        return !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty() && !fullName.isEmpty() && !phone.isEmpty() && !displayName.isEmpty();
    }

    private boolean _isPasswordStrong(String pass) {
        return pass.length() > 5;
    }


    private void _registerUser(String email, String pass, String pass2, String displayName, String fullName, String phone) {
        if (!_isFieldNotEmpty(email, pass, pass2, displayName, fullName, phone)) {
            _showError("e5");
            return;
        }

        if (!pass.equals(pass2)) {
            _showError("e4");
            return;
        }

        if (!_isPasswordStrong(pass)) {
            _showError("e3");
            return;
        }

        if (!_isEmailValid(email)) {
            _showError("e2");
            return;
        }

        String hashPass = HashUtils.hashPassword(pass);
        HashMap<String, String> keyPair= KeyStoreUtil.generateKeyPair();

        User user = new User(displayName, fullName, email, hashPass, Long.parseLong(phone), keyPair.get("Public"));

        CacheUtil.writePrivateKeysCache(this, keyPair.get("Private"), user);

        userApiHelper.registerUser(RegisterActivity.this, user);
    }
    private void _setOnClickListeners() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editEmail.getText().toString();
                pass = editPass.getText().toString();
                pass2 = editPass2.getText().toString();
                phone = editPhone.getText().toString();
                displayName = editDisplayName.getText().toString();
                fullName = editFullName.getText().toString();

                _registerUser(email, pass, pass2, displayName, fullName, phone);

            }
        });

        textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void _showError(String message) {
        ErrorUtil.GetErrorMessageInToast(message, RegisterActivity.this);
    }

}