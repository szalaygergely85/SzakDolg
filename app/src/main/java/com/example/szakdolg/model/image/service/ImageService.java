package com.example.szakdolg.model.image.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.util.ImageDatabaseUtil;
import com.example.szakdolg.model.image.api.ImageApiHelper;
import com.example.szakdolg.model.image.constans.ImageConstans;
import com.example.szakdolg.model.image.entity.ImageEntity;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserService;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageService {

   private Context context;

   private User currentUser;

   private ImageDatabaseUtil imageDatabaseUtil;

   public ImageService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      imageDatabaseUtil =
              new ImageDatabaseUtil(context, currentUser);
   }

   public void addImage(ImageEntity imageEntity) {
      Uri imageUri = Uri.parse(imageEntity.getImageUri());

      Glide
         .with(context)
         .asBitmap()
         .load(imageUri)
         .into(
            new CustomTarget<Bitmap>() {
               @Override
               public void onResourceReady(
                  @NonNull Bitmap bitmap,
                  @Nullable Transition<? super Bitmap> transition
               ) {
                  int maxSize = 4000;
                  if (
                     imageEntity.getTags() != null &&
                     imageEntity.getTags().equals(ImageConstans.TAG_PROFILE)
                  ) {
                     maxSize = 800;
                  }
                  Bitmap resizedBitmap = resizeImage(bitmap, maxSize);

                  // Save locally
                  File savedFile = saveImageLocally(
                     resizedBitmap,
                     imageEntity.getFileName()
                  );

                  Uri fileUri = Uri.fromFile(savedFile);

                  if (savedFile.exists()) {
                     imageEntity.setHeight(resizedBitmap.getHeight());
                     imageEntity.setWidth(resizedBitmap.getWidth());
                     imageEntity.setSize(resizedBitmap.getByteCount());

                     // Add to the database

                     imageDatabaseUtil.insertImageEntity(imageEntity);

                     //Upload

                     ImageApiHelper imageApiHelper = new ImageApiHelper();
                     imageApiHelper.uploadImage(savedFile, imageEntity);
                  } else {
                     Log.e(
                        AppConstants.LOG_TAG,
                        "File not found: " + savedFile.getAbsolutePath()
                     );
                  }
               }

               @Override
               public void onLoadCleared(@Nullable Drawable placeholder) {}
            }
         );
   }

   public ImageEntity createAndAddImage(
      Uri imageUri,
      Long userId,
      String mimeType,
      String tags
   ) {
      ImageEntity imageEntity = new ImageEntity(
         imageUri.toString(),
         userId,
         mimeType,
         System.currentTimeMillis(),
         ImageConstans.STATUS_PENDING,
         ImageConstans.TAG_PROFILE,
         UUIDUtil.UUIDGenerator()
      );

      if (tags != null && tags.equals(ImageConstans.TAG_PROFILE)) {
         imageEntity.setFileName(imageEntity.getUuid() + "_profile_image");
      } else {
         imageEntity.setFileName(imageEntity.getUuid());
      }
      addImage(imageEntity);
      return imageEntity;
   }

   public ImageEntity getImageByUserId(Long userId){
      UserService userService = new UserService(context);
      User user = userService.getUserByUserId(userId, currentUser);
      ImageEntity imageEntity= imageDatabaseUtil.getImageEntityByUuid(user.getProfilePictureUuid());
      return imageEntity;
   }

   private File saveImageLocally(Bitmap bitmap, String name) {
      File path = new File(context.getFilesDir() + "/Pictures/");
      if (!path.exists()) {
         path.mkdirs();
      }
      File file = new File(path, name + ".jpg");
      try (FileOutputStream out = new FileOutputStream(file)) {
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
         out.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return file;
   }

   public Bitmap resizeImage(Bitmap original, int maxSize) {
      int width = original.getWidth();
      int height = original.getHeight();

      float aspectRatio = (float) width / height;
      int newWidth, newHeight;

      if (width > height) {
         newWidth = maxSize;
         newHeight = Math.round(maxSize / aspectRatio);
      } else {
         newHeight = maxSize;
         newWidth = Math.round(maxSize * aspectRatio);
      }

      return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
   }
}
