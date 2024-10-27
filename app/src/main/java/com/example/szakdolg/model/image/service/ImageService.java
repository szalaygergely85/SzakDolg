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
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageService {

   private Context context;

   private User user;

   public ImageService(Context context, User user) {
      this.context = context;
      this.user = user;
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
                     ImageDatabaseUtil imageDatabaseUtil =
                        new ImageDatabaseUtil(context, user);
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

   public void createAndAddImage(
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
