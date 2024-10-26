package com.example.szakdolg.activity.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolg.activity.LoginActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class BaseActivity extends AppCompatActivity {
    protected String token;
    protected String userId;
    protected User currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the token and user ID
        token = SharedPreferencesUtil.getStringPreference(
                this,
                SharedPreferencesConstants.USERTOKEN
        );

        userId = SharedPreferencesUtil.getStringPreference(
                this,
                SharedPreferencesConstants.USER_ID
        );

        // Optionally handle cases where token or userId is missing
        if (token == null || userId == null) {
            _navigateToLogin();
        }else {
        currentUser = _getCurrentUser(token, userId);
        if (currentUser == null) {
            _navigateToLogin();
        }
    }
    }

    private void _navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private User _getCurrentUser(String token, String userId) {
        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                this,
                userId
        );

        return userDatabaseUtil.getCurrentUserByToken(token);
    }
}
