package com.example.szakdolg.activity.chat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.activity.chat.helper.ChatHelper;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.file.api.FileApiHelper;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.util.UserUtil;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

   private static final String TAG = "ChatActivity";
   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private Button btnSend;
   private MyEditText edtMess;
   private User currentUser;
   private User otherUser;

   private List<User> otherUsers;
   private MessageApiHelper messageApiHelper = new MessageApiHelper();
   private FileApiHelper fileApiHelper = new FileApiHelper();
   private ConversationApiHelper conversationApiHelper =
      new ConversationApiHelper();
   private ActionBar actionBar;

   private Handler handler = new Handler();
   private Runnable runnable;

   private String _token;

   private ChatHelper chatHelper;

   private ConversationDatabaseUtil conversationDatabaseUtil;

   private UserDatabaseUtil userDatabaseUtil;
   private MessageDatabaseUtil messageDatabaseUtil;

   private SharedPreferencesUtil sharedPreferencesUtil;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      _initView();

      _getSharedPrefAndIntentExtras();

      _startRepeatingTask();

      _setListeners();

      adapter = new ChatAdapter(this, currentUser);

      chatHelper =
      new ChatHelper(this, conversationId, currentUser, _token, adapter);

      List<User> otherUsers = chatHelper.getUsers(conversationId);

      adapter.setMessageEntries(chatHelper.getMessages(conversationId));

      adapter.setUsers(
         UserUtil.removeCurrentUserFromList(otherUsers, currentUser.getUserId())
      );

      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(this));

      Toolbar mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
      mToolbar.setTitle(" ");
      setSupportActionBar(mToolbar);

      actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setDisplayHomeAsUpEnabled(true);
      }
   }

   private void _getSharedPrefAndIntentExtras() {
      _token =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstants.USERTOKEN
      );
      currentUser =
      (User) this.getIntent()
         .getSerializableExtra(SharedPreferencesConstants.CURRENT_USER);

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
   }

   private void _startRepeatingTask() {
      runnable =
      new Runnable() {
         @Override
         public void run() {
            try {
               messageApiHelper.getNewMessages(
                  ChatActivity.this,
                  _token,
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

   private void _setListeners() {
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

      btnSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String content = edtMess.getText().toString();
               if (!content.isEmpty()) {
                  try {
                     if (otherUser != null && currentUser != null) {
                        String encryptedContentString =
                           EncryptionHelper.encrypt(
                              content,
                              CacheUtil.getPublicKeyFromCache(
                                 ChatActivity.this,
                                 otherUser.getEmail()
                              )
                           );

                        String encryptedContentSenderVersion =
                           EncryptionHelper.encrypt(
                              content,
                              currentUser.getPublicKey()
                           );

                        chatHelper.sendMessage(
                           MessageTypeConstants.MESSAGE,
                           encryptedContentSenderVersion,
                           encryptedContentString
                        );

                        edtMess.getText().clear();
                     }
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
