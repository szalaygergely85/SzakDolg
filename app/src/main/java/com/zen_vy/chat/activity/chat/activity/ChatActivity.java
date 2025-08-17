package com.zen_vy.chat.activity.chat.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.zen_vy.chat.MyEditText;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.chat.adapter.ChatAdapter;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.image.constans.ImageConstans;
import com.zen_vy.chat.models.image.service.ImageService;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.util.UserUtil;
import com.zen_vy.chat.util.DateTimeUtil;
import com.zen_vy.chat.util.UUIDUtil;
import com.zen_vy.chat.websocket.WebSocketService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import timber.log.Timber;

public class ChatActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      messageService = new MessageService(this, currentUser);

      MaterialToolbar toolbar = findViewById(R.id.chatToolbar);
      toolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );

      _initView();

      _getIntentExtras();

      _setListeners();


      adapter = new ChatAdapter(this, currentUser, chatRecView);

      _setMessageBoard(chatRecView, adapter);

      _setToolbarTitle(mToolbar);

      galleryLauncher =
      registerForActivityResult(
         new ActivityResultContracts.StartActivityForResult(),
         result -> {
            if (
               result.getResultCode() == RESULT_OK && result.getData() != null
            ) {
               Uri selectedImage = result.getData().getData();
               try {
                  _sendImage(selectedImage);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      );

      cameraLauncher =
      registerForActivityResult(
         new ActivityResultContracts.StartActivityForResult(),
         result -> {
            if (result.getResultCode() == RESULT_OK) {
               try {
                  _sendImage(imageUri);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      );
   }

   @Override
   protected void onResume() {
      super.onResume();
      LocalBroadcastManager
         .getInstance(this)
         .registerReceiver(
            _messageReceiver,
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
         .unregisterReceiver(_messageReceiver);
   }

   @Override
   protected void onStart() {
      super.onStart();


      if (!WebSocketService.isServiceRunning()) {
         _startingWebSocketService();
      }


      setMessagesRead();
   }

   public void setMessagesRead() {
      messageService.setMessagesAsReadByConversationId(
         conversation.getConversationId()
      );
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
   }

   private void _addNewMessageToEntries(MessageEntry message) {
      if (
         messageEntries.isEmpty() ||
         _isNewMessageEntryNewDay(
            messageEntries.get(messageEntries.size() - 1),
            message
         )
      ) {
         messageEntries.add(
            DateTimeUtil.toShortDateFormat(message.getTimestamp())
         );
      }

      messageEntries.add(message);

      int lastMessagePosition = adapter.getItemCount() - 1;
      adapter.notifyItemRangeChanged(lastMessagePosition - 1, 2);
      chatRecView.scrollToPosition(lastMessagePosition);
   }

   private void _captureImageWithCamera() {
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.TITLE, "New Picture");
      values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
      imageUri =
      getContentResolver()
         .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
      cameraLauncher.launch(intent);
   }

   private String _createTitleWithUsernames(List<User> users) {
      String title = "";
      for (User user : UserUtil.removeCurrentUserFromList(
         users,
         currentUser.getUserId()
      )) {
         if (users.indexOf(user) == 0) {
            title = user.getDisplayName();
         } else {
            title += " " + user.getDisplayName();
         }
      }
      return title;
   }

   private void _getIntentExtras() {
      ConversationDTO conversationDTO = (ConversationDTO) this.getIntent()
         .getSerializableExtra(IntentConstants.CONVERSATION_DTO);
      if (conversationDTO != null) {
         conversation = conversationDTO.getConversation();
         users = conversationDTO.getUsers();
         conversationId = conversation.getConversationId();
      } else {
         Timber.w("Could not fetch ConversationDTO from intent.");
      }
   }

   private void _initView() {
      imgAttach = findViewById(R.id.imgAttach);
      chatRecView = findViewById(R.id.recViewChat);
      imgSend = findViewById(R.id.imgSend);
      edtMess = findViewById(R.id.edtChatMes);
      mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
   }

   private boolean _isNewMessageEntryNewDay(
      Object prevMessage,
      MessageEntry newMessage
   ) {
      if (prevMessage instanceof MessageEntry) {
         return DateTimeUtil.isNewDay(
            ((MessageEntry) prevMessage).getTimestamp(),
            newMessage.getTimestamp()
         );
      }

      return false;
   }

   private void _pickImageFromGallery() {
      Intent intent = new Intent(
         Intent.ACTION_PICK,
         MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      );
      galleryLauncher.launch(intent);
   }

   private List<Object> _prepareMessageList(List<MessageEntry> messageEntries) {
      List<Object> sortedList = new ArrayList<>();
      long previousTimestamp = 0L;
      for (MessageEntry messageEntry : messageEntries) {
         if (
            DateTimeUtil.isNewDay(
               previousTimestamp,
               messageEntry.getTimestamp()
            )
         ) {
            sortedList.add(
               DateTimeUtil.toShortDateFormat(messageEntry.getTimestamp())
            );
         }

         sortedList.add(messageEntry);
         if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
            imageUrls.add(messageEntry.getContent());
         }
         previousTimestamp = messageEntry.getTimestamp();
      }
      return sortedList;
   }

   private void _sendImage(Uri uri) throws IOException {
      ImageService imageService = new ImageService(this, currentUser);

      imageService.addPicture(
         uri,
         currentUser.getUserId(),
         ImageConstans.TAG_MESSAGE,
         conversationId,
         new ImageService.ImageCallback<String>() {
            @Override
            public void onSuccess(String data) {
               try {
                  MessageEntry messageEntry = _sendMessage(
                     data,
                     MessageTypeConstants.IMAGE
                  );

                  _addNewMessageToEntries(messageEntry);
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   private MessageEntry _sendMessage(String content, int messageType) {
      MessageEntry messageEntry = new MessageEntry(
         null,
         conversation.getConversationId(),
         currentUser.getUserId(),
         System.currentTimeMillis(),
         null,
         false,
         messageType,
         content,
         UUIDUtil.UUIDGenerator(),
         false
      );

      MessageService messageService = new MessageService(this, currentUser);
      messageService.addMessage(
         messageEntry,
         new MessageService.MessageCallback<MessageEntry>() {
            @Override
            public void onSuccess(MessageEntry data) {
               Timber.i(
                  "Message sent to: %s",
                  messageEntry.getConversationId()
               );
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      return messageEntry;
   }

   private void _setListeners() {
      imgAttach.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String[] options = { "Choose from Gallery", "Take Photo" };
               new AlertDialog.Builder(ChatActivity.this)
                  .setTitle("Attach Image")
                  .setItems(
                     options,
                     (dialog, which) -> {
                        if (which == 0) {
                           _pickImageFromGallery();
                        } else {
                           _captureImageWithCamera();
                        }
                     }
                  )
                  .show();
            }
         }
      );

      imgSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String content = String.valueOf(edtMess.getText());
               if (!content.isEmpty()) {
                  try {
                     MessageEntry messageEntry = _sendMessage(
                        content,
                        MessageTypeConstants.MESSAGE
                     );

                     edtMess.getText().clear();
                     _addNewMessageToEntries(messageEntry);
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }
         }
      );
   }

   private void _setMessageBoard(
      RecyclerView chatRecView,
      ChatAdapter adapter
   ) {
      messageService.getMessagesByConversationId(
         conversation.getConversationId(),
         new MessageService.MessageCallback<List<MessageEntry>>() {
            @Override
            public void onSuccess(List<MessageEntry> results) {
               results.sort(
                  Comparator.comparingLong(MessageEntry::getTimestamp)
               );

               messageEntries = _prepareMessageList(results);
               adapter.setMessageEntries(messageEntries);
               adapter.setImageUrls(imageUrls);
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(this));
      chatRecView.scrollToPosition(adapter.getItemCount() - 1);
   }

   private void _setToolbarTitle(Toolbar mToolbar) {
      String conversationName = conversation.getConversationName();
      if (conversationName != null) {
         mToolbar.setTitle(conversation.getConversationName());
      } else {
         mToolbar.setTitle(_createTitleWithUsernames(users));
      }
   }

   private void _startingWebSocketService() {
      Intent serviceIntent = new Intent(this, WebSocketService.class);
      serviceIntent.putExtra(IntentConstants.CURRENT_USER, currentUser);
      serviceIntent.putExtra(IntentConstants.USER_TOKEN, token);
      startService(serviceIntent);
   }

   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private ImageView imgSend;
   private ImageView imgAttach;
   private MyEditText edtMess;
   private Toolbar mToolbar;
   private Conversation conversation;
   private List<User> users;
   private ActivityResultLauncher<Intent> galleryLauncher;
   private ActivityResultLauncher<Intent> cameraLauncher;
   private Uri imageUri;
   private MessageService messageService;

   private List<Object> messageEntries = new ArrayList<>();

   private ArrayList<String> imageUrls = new ArrayList<>();

   private final BroadcastReceiver _messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         MessageEntry message = (MessageEntry) intent.getSerializableExtra(
            "message"
         );
         runOnUiThread(() -> {
            if (message != null) {
               if (
                  Objects.equals(message.getConversationId(), conversationId)
               ) {
                  _addNewMessageToEntries(message);
               }
            }
         });
      }
   };
}
