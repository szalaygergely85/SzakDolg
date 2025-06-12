package com.zen_vy.chat.models.image.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.image.constans.ImageConstans;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

   public static String getImageName(String uuid, String tag) {
      String name = null;
      if (tag == ImageConstans.TAG_PROFILE) {
         name = uuid + "_profile_image.jpg";
      } else {
         name = uuid + ".jpg";
      }

      return name;
   }

   public static File saveImageLocally(
      Context context,
      Bitmap bitmap,
      String name
   ) {
      File path = new File(context.getFilesDir() + "/Pictures/");
      if (!path.exists()) {
         path.mkdirs();
      }
      File file = new File(path, name);
      try (FileOutputStream out = new FileOutputStream(file)) {
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
         out.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return file;
   }

   public static String getMimeType(Context context, Uri uri) {
      String mimeType = null;
      ContentResolver contentResolver = context.getContentResolver();
      mimeType = ((ContentResolver) contentResolver).getType(uri);
      return mimeType;
   }

   public static Bitmap resizeImage(Bitmap original, int maxSize) {
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

   public static int getMaxSize(ImageEntity imageEntity) {
      int maxSize = 4000;
      if (
         imageEntity.getTags() != null &&
         imageEntity.getTags().equals(ImageConstans.TAG_PROFILE)
      ) {
         maxSize = 800;
      }
      return maxSize;
   }

   public static String buildProfileImageUrl(Long userId) {
      String serverUrl = AppConstants.API_URL + "image/userid/";
      if (userId != null) {
         return serverUrl + userId;
      }
      return null;
   }

   public static String buildImageUrl(String uuid) {
      String serverUrl = AppConstants.API_URL + "image/uuid/";
      if (uuid != null) {
         return serverUrl + uuid;
      }
      return null;
   }

   public static File getFileFromUri(Context context, Uri uri)
      throws IOException {
      InputStream inputStream = context
         .getContentResolver()
         .openInputStream(uri);
      String fileName = "upload_image.jpg";
      File tempFile = new File(context.getCacheDir(), fileName);
      FileOutputStream outputStream = new FileOutputStream(tempFile);

      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
         outputStream.write(buffer, 0, bytesRead);
      }

      inputStream.close();
      outputStream.close();

      return tempFile;
   }

   public static GlideUrl getGlideUrlWithTokenHeader(
      String imageUrl,
      String token
   ) {
      return new GlideUrl(
         imageUrl,
         new LazyHeaders.Builder().addHeader("Authorization", token).build()
      );
   }
}
