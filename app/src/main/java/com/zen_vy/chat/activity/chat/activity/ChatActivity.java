package com.zen_vy.chat.activity.chat.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
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
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.MyEditText;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.chat.adapter.ChatAdapter;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.image.constans.ImageConstans;
import com.zen_vy.chat.models.image.service.ImageService;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.util.DateTimeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import timber.log.Timber;

public class ChatActivity extends BaseActivity {

   private ChatAdapter adapter;
   private Long conversationId;
   private RecyclerView chatRecView;
   private ImageView imgSend;
   private ImageView imgAttach;
   private MyEditText edtMess;
   private ChatActivityHelper chatActivityHelper;
   private Toolbar mToolbar;
   private Conversation conversation;
   private List<User> users;
   private ActivityResultLauncher<Intent> galleryLauncher;
   private ActivityResultLauncher<Intent> cameraLauncher;
   private Uri imageUri;
   private MessageService messageService;

   private List<Object> messageEntries;

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

      chatActivityHelper =
      new ChatActivityHelper(this, conversation, currentUser, users);

      adapter =
      new ChatAdapter(this, currentUser, chatRecView, chatActivityHelper);

      setMessageBoard(chatRecView, adapter);

      chatActivityHelper.setToolbarTitle(mToolbar);

      galleryLauncher =
      registerForActivityResult(
         new ActivityResultContracts.StartActivityForResult(),
         result -> {
            if (
               result.getResultCode() == RESULT_OK && result.getData() != null
            ) {
               Uri selectedImage = result.getData().getData();
               try {
                  sendImage(selectedImage);
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
                  sendImage(imageUri);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      );
   }

   private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
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
                  if (
                     messageEntries.size() == 0 ||
                     !isNewMessageEntryNewDay(
                        messageEntries.get(messageEntries.size() - 1),
                        message
                     )
                  ) {
                     messageEntries.add(message);
                     int lastMessagePosition = adapter.getItemCount() - 1;
                     adapter.notifyItemChanged(lastMessagePosition);
                     chatRecView.scrollToPosition(lastMessagePosition);
                  } else {
                     messageEntries.add(
                        createDateObject(message.getTimestamp())
                     );
                     messageEntries.add(message);

                     int lastMessagePosition = adapter.getItemCount() - 1;
                     adapter.notifyItemRangeChanged(lastMessagePosition - 1, 2);
                     chatRecView.scrollToPosition(lastMessagePosition);
                  }
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
      imgAttach = findViewById(R.id.imgAttach);
      chatRecView = findViewById(R.id.recViewChat);
      imgSend = findViewById(R.id.imgSend);
      edtMess = findViewById(R.id.edtChatMes);
      mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
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
                           pickImageFromGallery();
                        } else {
                           captureImageWithCamera();
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
      } else {
         Timber.w("Could not fetch ConversationDTO from intent.");
      }
   }

   private void pickImageFromGallery() {
      Intent intent = new Intent(
         Intent.ACTION_PICK,
         MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      );
      galleryLauncher.launch(intent);
   }

   private void captureImageWithCamera() {
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

   private void sendImage(Uri uri) throws IOException {
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
                  MessageEntry messageEntry = chatActivityHelper.sendMessage(
                     data,
                     MessageTypeConstants.IMAGE
                  );

                  adapter.addMessage(messageEntry);
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   private void setMessageBoard(RecyclerView chatRecView, ChatAdapter adapter) {
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
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(this));
      chatRecView.scrollToPosition(adapter.getItemCount() - 1);
   }

   private String createDateObject(Long timestamp) {
      DateFormat dateFormat = DateFormat.getDateInstance(
         DateFormat.SHORT,
         Locale.getDefault()
      );
      return dateFormat.format(new Date(timestamp));
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
            sortedList.add(createDateObject(messageEntry.getTimestamp()));
         }

         sortedList.add(messageEntry);
         previousTimestamp = messageEntry.getTimestamp();
      }
      return sortedList;
   }

   private boolean isNewMessageEntryNewDay(
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
}
