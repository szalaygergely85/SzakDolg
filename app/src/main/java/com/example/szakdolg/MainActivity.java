package com.example.szakdolg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuProfile:
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuSingOut:
                firebaseConnect.logoutUser();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseConnect.isUserSigned()) {
            Intent intent = new Intent(MainActivity.this, MessageBoardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User signed", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }
}