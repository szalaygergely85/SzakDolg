package com.example.szakdolg.activity.profilepicture.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.model.image.constans.ImageConstans;
import com.example.szakdolg.model.image.entity.ImageEntity;
import com.example.szakdolg.model.image.service.ImageService;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserService;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.IOException;

public class ProfilePictureActivityHelper {

   private Context context;
   private User currentUser;

   private ImageService imageService;

   public ProfilePictureActivityHelper(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.imageService = new ImageService(context, currentUser);
   }

   public void addImage(Uri imageUri) {
      String mimeType = getMimeType(imageUri);
      if (mimeType != null && (mimeType.startsWith("image/"))) {
        ImageEntity imageEntity = imageService.createAndAddImage(
            imageUri,
            currentUser.getUserId(),
            mimeType,
            ImageConstans.TAG_PROFILE
         );
         UserService userService = new UserService(context);
         currentUser.setProfilePictureUuid(imageEntity.getUuid());
         userService.updateUser(currentUser, currentUser);

      } else {
         Log.e(AppConstants.LOG_TAG, "Invalid image. Format not supported.");
      }
   }

   public String getMimeType(Uri uri) {
      String mimeType = null;
      ContentResolver contentResolver = context.getContentResolver();
      mimeType = ((ContentResolver) contentResolver).getType(uri);
      return mimeType;
   }

   public void setImageViewer(
      Context context,
      Uri uri,
      ShapeableImageView imageView
   ) throws IOException {
      Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(
         context.getContentResolver(),
         uri
      );

      // Resize the bitmap if needed
      Bitmap resizedBitmap = imageService.resizeImage(originalBitmap, 800);
      imageView.setImageURI(uri);
   }
}
