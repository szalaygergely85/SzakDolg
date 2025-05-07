package com.zen_vy.chat.models.image;

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
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.models.image.service.ImageService;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.user.entity.User;
import java.io.File;
import java.io.IOException;

public class ImageLocalHelper {

   public void saveImageToDisk(
      Context context,
      User currentUser,
      ImageEntity imageEntity,
      ImageSaveCallback callback
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
                  try {
                     Bitmap resizedBitmap = ImageUtil.resizeImage(
                        bitmap,
                        ImageUtil.getMaxSize(imageEntity)
                     );
                     File savedFile = ImageUtil.saveImageLocally(
                        context,
                        resizedBitmap,
                        imageEntity.getFileName()
                     );

                     if (savedFile.exists()) {
                        Uri fileUri = Uri.fromFile(savedFile);
                        imageEntity.setImageUri(fileUri.toString());
                        imageEntity.setHeight(resizedBitmap.getHeight());
                        imageEntity.setWidth(resizedBitmap.getWidth());
                        imageEntity.setSize(resizedBitmap.getByteCount());

                        new ImageService(context, currentUser)
                           .updateImage(imageEntity);

                        if (callback != null) {
                           callback.onImageSaved(imageEntity);
                        }
                     } else {
                        Log.e(
                           AppConstants.LOG_TAG,
                           "File not found: " + savedFile.getAbsolutePath()
                        );
                        if (callback != null) {
                           callback.onError(new IOException("File not saved"));
                        }
                     }
                  } catch (Exception e) {
                     if (callback != null) {
                        callback.onError(e);
                     }
                  }
               }

               @Override
               public void onLoadCleared(@Nullable Drawable placeholder) {}
            }
         );
   }

   public interface ImageSaveCallback {
      void onImageSaved(ImageEntity updatedImage);
      void onError(Exception e);
   }
}
