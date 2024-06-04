package com.example.szakdolg.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobScheduler;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.szakdolg.Chat;
import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.SQLConnect;
import com.example.szakdolg.message.MessageApiService;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    final Handler handler = new Handler();

    private ChatAdapter adapter;
    private ArrayList<Chat> messageList;

    private ArrayList<MessageEntry> messageEntryList;
    private Timer timer = new Timer();
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private SQLConnect sqlConnect;
    private String uID;
    private static final int JOB_ID = 201;

    private User user;
    private JobScheduler scheduler;

    private void initView() {
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();


        user = (User) this.getIntent().getSerializableExtra("user");

        Log.d(TAG, "onCreate: " + user.toString());

        Toolbar mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(mToolbar);


        //toolbar settings
/*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (sqlConnect.getNameFrContact(uID) != null) {
                actionBar.setTitle(sqlConnect.getNameFrContact(uID));
            }
        }*/


        MessageApiService messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);

        Call<ArrayList<MessageEntry>> call = messageApiService.getConversationMessages(1);

        call.enqueue(new Callback<ArrayList<MessageEntry>>(){
            @Override
            public void onResponse(Call<ArrayList<MessageEntry>> call, Response<ArrayList<MessageEntry>> response) {
                Log.e(TAG, ""+response.code());

                if (response.isSuccessful()) {
                    ArrayList<MessageEntry> messageEntryList = response.body();
                    if (messageEntryList!=null){

                        adapter.setMessageEntries(messageEntryList);
                    }

                } else {
                    Log.e(TAG, ""+response.code());
                    //TODO Handle the error
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MessageEntry>> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });

        messageList = new ArrayList<>();

        adapter = new ChatAdapter(this, user);
        adapter.setChats(messageList);

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = edtMess.getText().toString();
                if (!content.isEmpty()) {
                    if (user != null) {

                        LocalDateTime localDateTime = LocalDateTime.now();

                        MessageEntry messageEntry = new MessageEntry(1, user.getUserId(), System.currentTimeMillis(), content);

                        MessageApiService messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);

                        Call<MessageEntry> call = messageApiService.sendMessage(messageEntry);

                        call.enqueue(new Callback<MessageEntry>(){
                            @Override
                            public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
                                Log.e(TAG, ""+response.code());

                                if (response.isSuccessful()) {
                                    MessageEntry entry = response.body();


                                } else {
                                    Log.e(TAG, ""+response.code());
                                    //TODO Handle the error
                                }
                            }
                            @Override
                            public void onFailure(Call<MessageEntry> call, Throwable t) {
                                Log.e(TAG, ""+t.getMessage());
                            }
                        });
                        edtMess.getText().clear();
                    }
                } else {
                    Log.d(TAG, "onClick: Message field is empty");
                }
            }
        });
    }
}