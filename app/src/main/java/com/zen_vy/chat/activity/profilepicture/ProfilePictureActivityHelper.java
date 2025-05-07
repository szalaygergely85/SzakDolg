package com.zen_vy.chat.activity.profilepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.image.ImageCoordinatorService;
import com.zen_vy.chat.models.image.constans.ImageConstans;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.IOException;

public class ProfilePictureActivityHelper {

   private Context context;
   private User currentUser;

   private ImageCoordinatorService imageCoordinatorService;

   public ProfilePictureActivityHelper(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.imageCoordinatorService =
      new ImageCoordinatorService(context, currentUser);
   }

   public void addImage(Uri imageUri) {
      ImageEntity imageEntity = imageCoordinatorService.addPicture(
         imageUri,
         currentUser.getUserId(),
         ImageConstans.TAG_PROFILE
      );

      UserService userService = new UserService(context);
      currentUser.setProfilePictureUuid(imageEntity.getUuid());
      userService.updateUser(
         currentUser,
         currentUser.getToken(),
         new UserService.UserCallback<User>() {
            @Override
            public void onSuccess(User data) {
               Log.i(
                  AppConstants.LOG_TAG,
                  "User: " +
                  currentUser.getUserId() +
                  " profile pic updated with pic uuid: " +
                  imageEntity.getUuid()
               );
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
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
      Bitmap resizedBitmap = ImageUtil.resizeImage(originalBitmap, 800);
      imageView.setImageURI(uri);
   }
}
