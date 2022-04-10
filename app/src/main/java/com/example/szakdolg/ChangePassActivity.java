package com.example.szakdolg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassActivity extends AppCompatActivity {
    private static final String TAG = "ChangePassActivity";
    private EditText edtPass1;
    private EditText edtPass2;
    private EditText edtOldPass;
    private Button btnSend;
    private FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");

    private void initView() {
        edtPass1 = findViewById(R.id.edtChnPass1);
        edtPass2 = findViewById(R.id.edtChnPass2);
        edtOldPass = findViewById(R.id.edtChnPassOld);
        btnSend = findViewById(R.id.btnChnPass);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        initView();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.changeToolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Change Password");
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = edtPass1.getText().toString();
                String pass2 = edtPass2.getText().toString();
                String oldPass = edtOldPass.getText().toString();
                if (!pass1.isEmpty() && !pass2.isEmpty()) {
                    if (pass1.equals(pass2)) {
                        Log.d(TAG, "onClick: changing password");
                        firebaseConnect.changePassword(pass1, oldPass);
                        Toast.makeText(ChangePassActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Error.GetErrorMessageInToast("e4", ChangePassActivity.this);
                    }
                } else {
                    Error.GetErrorMessageInToast("e5", ChangePassActivity.this);
                }
            }
        });
    }
}