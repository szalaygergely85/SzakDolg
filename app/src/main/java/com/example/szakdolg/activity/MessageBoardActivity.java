package com.example.szakdolg.activity;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.MessageBoard;

import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.recviewadapter.MessageBoardRecAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.SQLConnect;
import com.example.szakdolg.message.MessageApiService;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageBoardActivity extends AppCompatActivity {
    private static final String TAG = "MessageB";
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    private MessageBoardRecAdapter adapter;
    private MaterialToolbar mToolbar;
    private User user;
    private void initView() {
        contactsButton = findViewById(R.id.btnMesBrdNew);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);

        initView();

        user = (User) this.getIntent().getSerializableExtra("logged_user");

        Log.d(TAG, "onCreate: " + user.toString());

        mToolbar = (MaterialToolbar) findViewById(R.id.messageBoardToolbar);
        setSupportActionBar(mToolbar);

        messageBoardRecView = findViewById(R.id.messageBoardRecView);

        ArrayList<MessageBoard> messageBoard = new ArrayList<>();
        adapter = new MessageBoardRecAdapter(this);
        adapter.setMessageB(messageBoard);
        adapter.setUser(user);
        messageBoardRecView.setAdapter(adapter);
        messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));

        MessageApiService messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);

        Call<ArrayList<MessageBoard>> messagesCall= messageApiService.getLatestMessages(SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.USERTOKEN));

        messagesCall.enqueue(new Callback<ArrayList<MessageBoard>>(){
            @Override
            public void onResponse(Call<ArrayList<MessageBoard>> call, Response<ArrayList<MessageBoard>> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, ""+response.code());
                    ArrayList<MessageBoard> messageBoard = response.body();

                    adapter.setMessageB(messageBoard);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MessageBoard>> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                intent.putExtra("logged_user", user);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuProfile:
                intent = new Intent(MessageBoardActivity.this, ProfileActivity.class);
                intent.putExtra("logged_user", user);
                startActivity(intent);
                break;
            case R.id.menuContacts:
                intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                intent.putExtra("logged_user", user);
                startActivity(intent);
                break;
            case R.id.menuSingOut:

            default:
                break;
        }
        return false;
    }

}
