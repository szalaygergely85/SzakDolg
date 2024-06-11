package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.szakdolg.constans.MessageConstans;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.time.LocalDateTime;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ChatAdapter adapter;
    private Long conversationId;
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private User loggedUser;
    private User participant;
    private MessageApiHelper messageApiHelper = new MessageApiHelper();
    private ConversationApiHelper conversationApiHelper = new ConversationApiHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        loggedUser = (User) this.getIntent().getSerializableExtra(SharedPreferencesConstans.LOGGED_USER);
        participant = (User) this.getIntent().getSerializableExtra("participant_user");
        conversationId = this.getIntent().getLongExtra(SharedPreferencesConstans.CONVERSATION_ID, 0);
        String publicKey = SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.PUBLIC_KEY + conversationId);
        String privateKey = SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.PRIVATE_KEY+conversationId);

        if (publicKey.isEmpty() || publicKey.isEmpty()){
            conversationApiHelper.checkConversationKeyStatus(conversationId, loggedUser);
        }

        adapter = new ChatAdapter(this, loggedUser);

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));


        Toolbar mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        mToolbar.setTitle(" ");
        setSupportActionBar(mToolbar);

        //toolbar settings

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        messageApiHelper.reloadMessages(conversationId, adapter, actionBar);
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
                        if (loggedUser != null) {

                            LocalDateTime localDateTime = LocalDateTime.now();

                            MessageEntry messageEntry = new MessageEntry(conversationId, loggedUser.getUserId(), System.currentTimeMillis(), content, MessageConstans.TYPE_MESSAGE);

                            messageApiHelper.sendMessage(conversationId, messageEntry, adapter);

                            edtMess.getText().clear();
                        }
                    } else {
                        Log.d(TAG, "onClick: Message field is empty");
                    }
                }
            });
        }

    private void _initView() {
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);
    }
}