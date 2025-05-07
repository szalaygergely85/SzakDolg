package com.zen_vy.chat.activity.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.MyEditText;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.chat.adapter.ChatAdapter;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ChatActivity extends BaseActivity {

   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private ImageView imgSend;
   private MyEditText edtMess;
   private ChatActivityHelper chatActivityHelper;
   private Toolbar mToolbar;
   private ConversationService conversationService;
   private Conversation conversation;
   private List<User> users;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      MaterialToolbar toolbar = findViewById(R.id.chatToolbar);
      toolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );

      _initView();

      _getIntentExtras();

      _setListeners();

      chatActivityHelper =
      new ChatActivityHelper(this, conversation, currentUser, users);

      adapter =
      new ChatAdapter(this, currentUser, chatRecView, chatActivityHelper);

      chatActivityHelper.setMessageBoard(chatRecView, adapter);

      chatActivityHelper.setToolbarTitle(mToolbar);
   }



   private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         MessageEntry message = (MessageEntry) intent.getSerializableExtra(
            "message"
         );
         runOnUiThread(()->{
            if (message != null) {
               if (Objects.equals(message.getConversationId(), conversationId)) {
                  adapter.addMessage(message);
               }
            }
         });

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
	mToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
*/
      imgSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String content = edtMess.getText().toString();
               if (!content.isEmpty()) {
                  try {

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

   private void _getIntentExtras() {
      ConversationDTO conversationDTO = (ConversationDTO) this.getIntent()
              .getSerializableExtra(IntentConstants.CONVERSATION_DTO);
       if (conversationDTO != null) {
          conversation = conversationDTO.getConversation();
          users = conversationDTO.getUsers();
          conversationId = conversation.getConversationId();
       }else{
          Timber.w("Could not fetch ConversationDTO from intent.");
       }
   }
   /*
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
}*/
}
