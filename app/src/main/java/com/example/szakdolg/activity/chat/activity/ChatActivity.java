package com.example.szakdolg.activity.chat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.message.constans.MessageTypeConstants;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.file.api.FileApiHelper;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;

public class ChatActivity extends BaseActivity {

   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private ImageView imgSend;
   private MyEditText edtMess;

   private FileApiHelper fileApiHelper = new FileApiHelper();

   private Handler handler = new Handler();

   private ChatActivityHelper chatActivityHelper;

   private Toolbar mToolbar;

   private ConversationCoordinatorService conversationCoordinatorService;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      _initView();

      _getIntentExtras();

      //_startRepeatingTask();

      _setListeners();

      adapter = new ChatAdapter(this, currentUser, chatRecView);

      chatActivityHelper =
      new ChatActivityHelper(this, conversationId, currentUser);

      chatActivityHelper.setMessageBoard(chatRecView, adapter);

      chatActivityHelper.setToolbarTitle(mToolbar, conversationId);
   }

   private void _getIntentExtras() {
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
   }

   private void _initView() {
      chatRecView = findViewById(R.id.recViewChat);
      imgSend = findViewById(R.id.imgSend);
      edtMess = findViewById(R.id.edtChatMes);
      mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
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

      imgSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String content = edtMess.getText().toString();
               if (!content.isEmpty()) {
                  try {
                     chatActivityHelper.sendMessage(
                        content,
                        MessageTypeConstants.MESSAGE
                     );
                     edtMess.getText().clear();
                     chatActivityHelper.reloadMessages(adapter);
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }
         }
      );
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
            null,
            conversationId,
            currentUser.getUserId(),
            System.currentTimeMillis(),
            null,
            false,
            MessageTypeConstants.IMAGE,
            uUId + "." + FileUtil.getFileExtensionFromUri(uri),
            UUIDUtil.UUIDGenerator()
         );
         FileUtil.saveFileFromUri(
            uri,
            file,
            () -> fileApiHelper.uploadFile(file, messageEntry)
         );
         /*
		messageApiHelper.reloadMessages(
			ChatActivity.this,
			conversationId,
			adapter,
			currentUser
		);*/
      })
         .start();
   }
}
