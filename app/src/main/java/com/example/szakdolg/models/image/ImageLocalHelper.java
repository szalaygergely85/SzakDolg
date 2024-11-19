package com.example.szakdolg.models.image;

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
import com.example.szakdolg.models.image.entity.ImageEntity;
import com.example.szakdolg.models.image.service.ImageService;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.user.entity.User;
import java.io.File;

public class ImageLocalHelper {

   public void saveImageToDisk(
      Context context,
      User currentUser,
      ImageEntity imageEntity
   ) {
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
                  Bitmap resizedBitmap = ImageUtil.resizeImage(
                     bitmap,
                     ImageUtil.getMaxSize(imageEntity)
                  );

                  // Save locally
                  File savedFile = ImageUtil.saveImageLocally(
                     context,
                     resizedBitmap,
                     imageEntity.getFileName()
                  );

                  Uri fileUri = Uri.fromFile(savedFile);

                  if (savedFile.exists()) {
                     imageEntity.setImageUri(fileUri.toString());
                     imageEntity.setHeight(resizedBitmap.getHeight());
                     imageEntity.setWidth(resizedBitmap.getWidth());
                     imageEntity.setSize(resizedBitmap.getByteCount());

                     ImageService imageService = new ImageService(
                        context,
                        currentUser
                     );
                     imageService.updateImage(imageEntity);
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
}
