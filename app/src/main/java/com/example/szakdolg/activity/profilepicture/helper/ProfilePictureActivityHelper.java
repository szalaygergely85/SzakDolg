package com.example.szakdolg.activity.profilepicture.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.model.image.constans.ImageConstans;
import com.example.szakdolg.model.image.service.ImageService;
import com.example.szakdolg.model.user.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.IOException;

public class ProfilePictureActivityHelper {

   private Context context;
   private User user;

   ImageService imageService;

   public ProfilePictureActivityHelper(Context context, User user) {
      this.context = context;
      this.user = user;
      this.imageService = new ImageService(context, user);
   }

   public void addImage(Uri imageUri) {
      String mimeType = getMimeType(imageUri);
      if (mimeType != null && (mimeType.startsWith("image/"))) {
         imageService.createAndAddImage(
            imageUri,
            user.getUserId(),
            mimeType,
            ImageConstans.TAG_PROFILE
         );
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
