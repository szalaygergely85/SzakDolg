package com.example.szakdolg.activity.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bumptech.glide.Glide;
import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.image.constans.ImageConstans;
import com.example.szakdolg.models.image.service.ImageService;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.user.entity.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

   private static final int PHOTO_PICKER_REQUEST_CODE = 101;

   private MaterialToolbar chatToolbar;
   private EditText profeditStatus;
   private ImageView iconEditStatus;
   private ImageView iconEditPic;

   private ShapeableImageView profPic;

   private Button deleteContact;
   private Button sendMessage;
   private User user;
   private String action;

   private Uri imageUri;

   private Contact contact;
   private ConversationService conversationService;
   private ContactService contactService;

   private ImageService imageService;

   private void initView() {
      deleteContact = findViewById(R.id.profBtnDeleteContact);
      sendMessage = findViewById(R.id.profBtnSendMsg);

      profPic = findViewById(R.id.profPic);

      chatToolbar = findViewById(R.id.chatToolbar);

      profeditStatus = findViewById(R.id.profeditStatus);
      iconEditStatus = findViewById(R.id.iconEditStatus);
      iconEditPic = findViewById(R.id.iconEditPic);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);

      initView();

      conversationService = new ConversationService(this, currentUser);
      contactService = new ContactService(this, currentUser);
      imageService = new ImageService(this, currentUser);

      action = getIntent().getStringExtra(IntentConstants.PROFILE_ACTION);

      if (action.equals(ProfileConstants.VIEW_CONTACT)) {
         contact =
         (Contact) this.getIntent()
            .getSerializableExtra(IntentConstants.CONTACT);
         user =
         (User) this.getIntent().getSerializableExtra(IntentConstants.USER);

         deleteContact.setVisibility(View.VISIBLE);
         sendMessage.setVisibility(View.VISIBLE);

         iconEditStatus.setVisibility(View.GONE);
         iconEditPic.setVisibility(View.GONE);

         profeditStatus.setInputType(InputType.TYPE_NULL);
      }

      if (action.equals(ProfileConstants.VIEW_PROFILE)) {
         user = currentUser;
         deleteContact.setVisibility(View.GONE);
         sendMessage.setVisibility(View.GONE);
         profeditStatus.setInputType(InputType.TYPE_CLASS_TEXT);

         addEditProfileImageListener();
      }

      chatToolbar.setTitle(user.getDisplayName());

      chatToolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );

      String status = user.getStatus();
      if (status.isEmpty()) {
         profeditStatus.setText("Tap to set a status...");
      } else {
         profeditStatus.setText("Status");
      }

      //Set image

      String imageUrl = ImageUtil.buildProfileImageUrl(user.getUserId());
      Glide
         .with(this)
         .load(imageUrl)
         .placeholder(R.drawable.ic_blank_profile)
         .error(R.drawable.ic_blank_profile)
         .into(profPic);
   }

   @Override
   protected void onStart() {
      super.onStart();

      deleteContact.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               contactService.deleteContact(
                  contact,
                  new ContactService.ContactCallback<Void>() {
                     @Override
                     public void onSuccess(Void data) {}

                     @Override
                     public void onError(Throwable t) {}
                  }
               );

               finish();
            }
         }
      );

      sendMessage.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               List<Long> participants = new ArrayList<>();
               participants.add(currentUser.getUserId());
               participants.add(user.getUserId());

               conversationService.addConversationByUserId(
                  participants,
                  new ConversationService.ConversationCallback<
                     ConversationDTO
                  >() {
                     @Override
                     public void onSuccess(ConversationDTO data) {
                        Intent intent = new Intent(
                           ProfileActivity.this,
                           ChatActivity.class
                        );
                        intent.putExtra(
                           IntentConstants.CONVERSATION_ID,
                           data.getConversation().getConversationId()
                        );
                        intent.putExtra(IntentConstants.CONVERSATION_DTO, data);
                        startActivity(intent);
                     }

                     @Override
                     public void onError(Throwable t) {}
                  }
               );
            }
         }
      );
   }

   private void addEditProfileImageListener() {
      profPic.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openImageChooser();
            }
         }
      );
   }

   public void openImageChooser() {
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType("image/*"); // Set the type to images
      pickImageLauncher.launch(intent); // Launch the image picker using the ActivityResultLauncher
   }

   ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
         if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            imageUri = result.getData().getData();
            try {
               Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(
                  getContentResolver(),
                  imageUri
               );

               // Resize the bitmap if needed
               Bitmap resizedBitmap = ImageUtil.resizeImage(
                  originalBitmap,
                  800
               );

               Glide
                  .with(this)
                  .load(resizedBitmap)
                  .placeholder(R.drawable.ic_blank_profile)
                  .error(R.drawable.ic_blank_profile)
                  .into(profPic);

               imageService.addPicture(
                  imageUri,
                  currentUser.getUserId(),
                  ImageConstans.TAG_PROFILE,
                  new ImageService.ImageCallback() {
                     @Override
                     public void onSuccess(Object data) {}

                     @Override
                     public void onError(Throwable t) {}
                  }
               );
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }
      }
   );
}
