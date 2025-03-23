package com.example.szakdolg.activity.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.file.api.FileApiHelper;
import com.example.szakdolg.models.message.MessageService;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;
import java.util.List;

public class ChatActivity extends BaseActivity {

   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private ImageView imgSend;
   private MyEditText edtMess;
   private FileApiHelper fileApiHelper = new FileApiHelper();
   private ChatActivityHelper chatActivityHelper;
   private Toolbar mToolbar;
   private ConversationService conversationService;
   private Conversation conversation;
   private List<User> users;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      _initView();

      _getIntentExtras();

      _setListeners();

      adapter = new ChatAdapter(this, currentUser, chatRecView);

      chatActivityHelper =
      new ChatActivityHelper(this, conversation, currentUser, users);

      chatActivityHelper.setMessageBoard(chatRecView, adapter);

      chatActivityHelper.setToolbarTitle(mToolbar, conversationId);
   }

   private void _getIntentExtras() {

      ConversationDTO conversationDTO = (ConversationDTO) this.getIntent().getSerializableExtra(IntentConstants.CONVERSATION_DTO);
      conversation = conversationDTO.getConversation();
      users = conversationDTO.getUsers();
      conversationId = conversation.getConversationId();
   }

   private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         MessageEntry message = (MessageEntry) intent.getSerializableExtra(
            "message"
         );
         if (message != null) {
            if (message.getConversationId() == conversationId) {
               message.setRead(true);
               adapter.addMessage(message);
               adapter.notifyDataSetChanged();
               chatRecView.scrollToPosition(adapter.getItemCount() - 1);
            }
         }
      }
   };

   @Override
   protected void onResume() {
      super.onResume();
      LocalBroadcastManager
         .getInstance(this)
         .registerReceiver(
            messageReceiver,
            new IntentFilter(
               "com.example.szakdolg.models.message.entity.MessageBroadCast"
            )
         );
   }

   @Override
   protected void onPause() {
      super.onPause();
      LocalBroadcastManager
         .getInstance(this)
         .unregisterReceiver(messageReceiver);
   }

   @Override
   protected void onStart() {
      super.onStart();

      chatActivityHelper.setMessagesRead();
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
                     //TODO Maximalize text long
                     MessageEntry messageEntry = chatActivityHelper.sendMessage(
                        content,
                        MessageTypeConstants.MESSAGE
                     );

                     edtMess.getText().clear();
                     adapter.addMessage(messageEntry);

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
      })
         .start();
   }
}
