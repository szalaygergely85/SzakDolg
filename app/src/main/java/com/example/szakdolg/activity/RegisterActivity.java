package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.szakdolg.util.ErrorUtil;

import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserApiService;
import com.example.szakdolg.user.UserToken;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPass;
    private EditText editPass2;
    private EditText editName;
    private EditText editPhone;
    private Button btnReg;

    private static final String TAG = "RegisterActivity";

    /**
     * method is used for checking valid email id format.
     *
     * @param email email address
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
                                User user = new User(name, name, email, pass, Long.parseLong(phone));

                                UserApiService userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);

                                Call<UserToken> call = userApiService.createUser(user);
                                call.enqueue(new Callback<UserToken>() {
                                    @Override
                                    public void onResponse(Call<UserToken> call, Response<UserToken> response) {

                                        Log.e(TAG, "" + response.code());

                                        if (response.isSuccessful()) {
                                            UserToken userToken = response.body();

                                            if (userToken != null) {

                                                SharedPreferencesUtil.setStringPreference(RegisterActivity.this, SharedPreferencesConstans.USERTOKEN, userToken.getToken());

                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                                                startActivity(intent);

                                                Toast.makeText(RegisterActivity.this, "A user signed in", Toast.LENGTH_SHORT).show();

                                                finish();
                                            }


                                        } else {
                                            Log.e(TAG, "" + response.code());
                                            //TODO Handle the error
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserToken> call, Throwable t) {

                                    }
                                });




                            } else {
                                ErrorUtil.GetErrorMessageInToast("e2", RegisterActivity.this);
                            }
                        } else {
                            ErrorUtil.GetErrorMessageInToast("e3", RegisterActivity.this);
                        }
                    } else {
                        ErrorUtil.GetErrorMessageInToast("e4", RegisterActivity.this);
                    }
                } else {
                    ErrorUtil.GetErrorMessageInToast("e5", RegisterActivity.this);
                }


            }
        });

    }

}