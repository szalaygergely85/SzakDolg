package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.constans.IntentConstans;
import com.example.szakdolg.constans.MessageConstans;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.adapter.ChatAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ChatAdapter adapter;
    private Long conversationId;
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private User currentUser;
    private ConversationContent conversationContent;
    private User otherUser;
    private MessageApiHelper messageApiHelper = new MessageApiHelper();
    private ConversationApiHelper conversationApiHelper = new ConversationApiHelper();
    private ActionBar actionBar;

    private Handler handler = new Handler();
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        _initView();
        _startRepeatingTask();


        currentUser = (User) this.getIntent().getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);
        conversationId = this.getIntent().getLongExtra(SharedPreferencesConstans.CONVERSATION_ID, 0);
        conversationContent = (ConversationContent) this.getIntent().getSerializableExtra(IntentConstans.CONVERSATION_CONTENT);
        otherUser = UserUtil.removeCurrentUserFromList(conversationContent.getParticipants(), currentUser.getUserId());


        adapter = new ChatAdapter(this, currentUser);
        adapter.setMessageEntries(conversationContent.getMessages());

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));


        Toolbar mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        mToolbar.setTitle(" ");
        setSupportActionBar(mToolbar);


        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = edtMess.getText().toString();
                if (!content.isEmpty()) {

                    try {
                        if (otherUser != null && currentUser != null) {
                            String encryptedContentString =
                                    EncryptionHelper.encrypt(content, CacheUtil.getPublicKeyFromCache(ChatActivity.this, otherUser.getEmail()));

                            String encryptedContentSenderVersion = EncryptionHelper.encrypt(content, currentUser.getPublicKey());

                            Log.e(TAG, EncryptionHelper.decrypt(encryptedContentSenderVersion, CacheUtil.getPrivateKeyFromCache(ChatActivity.this, currentUser)));

                            MessageEntry messageEntry = new MessageEntry(conversationId, currentUser.getUserId(), System.currentTimeMillis(), encryptedContentString, MessageConstans.TYPE_MESSAGE, encryptedContentSenderVersion);

                            messageApiHelper.sendMessage(conversationId, messageEntry, adapter);

                            messageApiHelper.reloadMessages(conversationId, adapter, actionBar);

                            edtMess.getText().clear();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _stopRepeatingTask();
    }

    private void _initView() {
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);

        btnSend.setActivated(false);
    }

    private void _startRepeatingTask() {
        if (conversationId!=null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        messageApiHelper.reloadMessages(conversationId, adapter, actionBar);
                    } finally {
                        handler.postDelayed(runnable, 10000);
                    }
                }
            };

            runnable.run();
        }
    }
    private void _stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }
}