package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.widget.Button;

import com.example.szakdolg.DTO.ConversationContent;

import com.example.szakdolg.MyEditText;
import com.example.szakdolg.constans.IntentConstans;
import com.example.szakdolg.constans.MessageTypeConstans;

import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.file.apihelper.FileApiHelper;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.adapter.ChatAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.UUIDUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ChatAdapter adapter;
    private Long conversationId;
    private RecyclerView chatRecView;
    private Button btnSend;
    private MyEditText edtMess;
    private User currentUser;
    private ConversationContent conversationContent;
    private User otherUser;
    private MessageApiHelper messageApiHelper = new MessageApiHelper();
    private FileApiHelper fileApiHelper = new FileApiHelper();
    private ConversationApiHelper conversationApiHelper = new ConversationApiHelper();
    private ActionBar actionBar;

    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        _initView();
      //  _startRepeatingTask();

        edtMess.setKeyBoardInputCallbackListener(new MyEditText.KeyBoardInputCallbackListener() {
            @Override
            public void onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {

            _sendFile(inputContentInfo.getLinkUri());

            }
        });



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

                            _sendMessage(MessageTypeConstans.MESSAGE, encryptedContentSenderVersion, encryptedContentString);

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

            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (conversationId!=null && adapter!=null) {
                            messageApiHelper.reloadMessages(conversationId, adapter, actionBar);
                        }
                    } finally {
                        handler.postDelayed(runnable, 10000);
                    }
                }
            };

            runnable.run();

    }

    private void _sendMessage(int messageType, String encryptedContentSenderVersion, String encryptedContentString){

        MessageEntry messageEntry = new MessageEntry(conversationId, currentUser.getUserId(), System.currentTimeMillis(), encryptedContentString, messageType, encryptedContentSenderVersion);

        messageApiHelper.sendMessage(conversationId, messageEntry, adapter);
        messageApiHelper.reloadMessages(conversationId, adapter, actionBar);

    }
    private void _stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }


    private void _sendFile(Uri uri){
        new Thread(() -> {

            String uUId = UUIDUtil.UUIDGenerator();

            File file = new File(this.getFilesDir() + "/Pictures/" + uUId + FileUtil.getFileExtensionFromUri(uri));

            FileUtil.saveFileFromUri(uri, file, () -> fileApiHelper.uploadFile(file));



        }).start();

    }
}