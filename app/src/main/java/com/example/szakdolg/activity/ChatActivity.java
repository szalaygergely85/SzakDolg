package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
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
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.conversation.ConversationApiService;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.SQLConnect;
import com.example.szakdolg.message.MessageApiService;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    final Handler handler = new Handler();

    private ChatAdapter adapter;
    private ArrayList<Chat> messageList;

    private Long conversationId;
    private ArrayList<MessageEntry> messageEntryList;
    private Timer timer = new Timer();
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private SQLConnect sqlConnect;
    private String uID;
    private static final int JOB_ID = 201;

    private User user;
    private User participant;
    private JobScheduler scheduler;
    private MessageApiHelper messageApiHelper = new MessageApiHelper();

    private ConversationApiHelper conversationApiHelper = new ConversationApiHelper();


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

        user = (User) this.getIntent().getSerializableExtra("logged_user");
        participant = (User) this.getIntent().getSerializableExtra("participant_user");
        conversationId = this.getIntent().getLongExtra("conversationId", 0);

        adapter = new ChatAdapter(this, user);

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));

        if(conversationId==0){
            if (participant!=null) {

                List<User> participants = new ArrayList<>();
                participants.add(user);
                participants.add(participant);

             conversationApiHelper.addConversation(participants, adapter);
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(mToolbar);

        //toolbar settings

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(participant.getFirstName());

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageApiHelper.reloadMessages(conversationId, adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = edtMess.getText().toString();
                if (!content.isEmpty()) {
                    if (user != null) {

                        LocalDateTime localDateTime = LocalDateTime.now();

                        MessageEntry messageEntry = new MessageEntry(conversationId, user.getUserId(), System.currentTimeMillis(), content);

                        messageApiHelper.sendMessage(conversationId, messageEntry, adapter);

                        edtMess.getText().clear();
                    }
                } else {
                    Log.d(TAG, "onClick: Message field is empty");
                }
            }
        });
    }
}