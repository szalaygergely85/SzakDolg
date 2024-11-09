package com.example.szakdolg.activity.chat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.activity.chat.helper.ChatActivityHelper;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.model.conversation.ConversationCoordinatorService;
import com.example.szakdolg.model.file.api.FileApiHelper;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;

public class ChatActivity extends BaseActivity {
   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private Button btnSend;
   private MyEditText edtMess;

   private MessageApiHelper messageApiHelper;
   private FileApiHelper fileApiHelper = new FileApiHelper();

   private ActionBar actionBar;

   private Handler handler = new Handler();
   private Runnable runnable;

   private ChatActivityHelper chatActivityHelper;

   private Toolbar mToolbar;

   private  ConversationCoordinatorService conversationCoordinatorService;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      _initView();

      _getSharedPrefAndIntentExtras();

      //_startRepeatingTask();

      _setListeners();

      messageApiHelper = new MessageApiHelper(this, currentUser);

      adapter = new ChatAdapter(this, currentUser);

      conversationCoordinatorService = new ConversationCoordinatorService(this, currentUser);

      chatActivityHelper =
      new ChatActivityHelper(this, conversationCoordinatorService.getConversation(conversationId), currentUser, token, adapter);

      chatActivityHelper.setMessageBoard(chatRecView, adapter);

      mToolbar.setTitle(" ");
      setSupportActionBar(mToolbar);

      actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setDisplayHomeAsUpEnabled(true);
      }
   }

   private void _getSharedPrefAndIntentExtras() {
      conversationId =
      this.getIntent().getLongExtra(IntentConstants.CONVERSATION_ID, 0);
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
      mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
   }

   private void _startRepeatingTask() {
      runnable =
      new Runnable() {
         @Override
         public void run() {
            try {
               messageApiHelper.getNewMessages(
                  ChatActivity.this,
                  token,
                  currentUser,
                  () -> {
                     messageApiHelper.reloadMessages(
                        ChatActivity.this,
                        conversationId,
                        adapter,
                        currentUser
                     );
                  }
               );
            } finally {
               handler.postDelayed(runnable, 15000);
            }
         }
      };

      runnable.run();
   }

   private void _setListeners() {/*
      edtMess.setKeyBoardInputCallbackListener(
         new MyEditText.KeyBoardInputCallbackListener() {
            @Override
            public void onCommitContent(
               InputContentInfoCompat inputContentInfo,
               int flags,
               Bundle opts
            ) {
               _sendFile(inputContentInfo.getLinkUri());
            }
         }
      );
      */
      btnSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String content = edtMess.getText().toString();
               if (!content.isEmpty()) {
                  try {

                     //TODO itt tartok!
                     chatActivityHelper.sendMessage(content, MessageTypeConstants.MESSAGE);

                        edtMess.getText().clear();

                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }
         }
      );
   }

   private void _stopRepeatingTask() {
      handler.removeCallbacks(runnable);
   }

   private void _sendFile(Uri uri) {
      new Thread(() -> {
         String uUId = UUIDUtil.UUIDGenerator();

         File file = new File(
            this.getFilesDir() +
            "/Pictures/" +
            uUId +
            "." +
            FileUtil.getFileExtensionFromUri(uri)
         );

         MessageEntry messageEntry = new MessageEntry(
            conversationId,
            currentUser.getUserId(),
            System.currentTimeMillis(),
            uUId + "." + FileUtil.getFileExtensionFromUri(uri),
            MessageTypeConstants.IMAGE,
            null,
            UUIDUtil.UUIDGenerator()
         );
         FileUtil.saveFileFromUri(
            uri,
            file,
            () -> fileApiHelper.uploadFile(file, messageEntry)
         );
         messageApiHelper.reloadMessages(
            ChatActivity.this,
            conversationId,
            adapter,
            currentUser
         );
      })
         .start();
   }
}
