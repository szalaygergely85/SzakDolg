package com.example.szakdolg.activity.profilepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.example.szakdolg.model.image.ImageCoordinatorService;
import com.example.szakdolg.model.image.constans.ImageConstans;
import com.example.szakdolg.model.image.entity.ImageEntity;
import com.example.szakdolg.model.image.util.ImageUtil;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserService;
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
      userService.updateUser(currentUser, currentUser);
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
