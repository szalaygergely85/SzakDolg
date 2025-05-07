package com.zen_vy.chat.models.image;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.zen_vy.chat.activity.base.BaseService;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.image.api.ImageApiHelper;
import com.zen_vy.chat.models.image.constans.ImageConstans;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.models.image.service.ImageService;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.util.FileUtil;
import com.zen_vy.chat.util.UUIDUtil;
import java.io.File;
import java.io.IOException;

public class ImageCoordinatorService extends BaseService {

   private final ImageService imageService;
   private final ImageApiHelper imageApiHelper;

   public ImageCoordinatorService(Context context, User currentUser) {
      super(context, currentUser);
      this.imageService = new ImageService(context, currentUser);
      this.imageApiHelper = new ImageApiHelper();
   }

   public ImageEntity addPicture(Uri imageUri, Long userId, String tags) {
      String mimeType = ImageUtil.getMimeType(context, imageUri);

      if (mimeType != null && (mimeType.startsWith("image/"))) {
         String uuid = UUIDUtil.UUIDGenerator();
         String fileName = ImageUtil.getImageName(uuid, tags);

         ImageEntity imageEntity = new ImageEntity(
            fileName,
            userId,
            imageUri.toString(),
            ImageUtil.getMimeType(context, imageUri),
            System.currentTimeMillis(),
            ImageConstans.STATUS_PENDING,
            tags,
            uuid
         );

         //Database

         // imageService.addImage(imageEntity);

         //File

         /*   ImageLocalHelper imageLocalHelper = new ImageLocalHelper();
		imageLocalHelper.saveImageToDisk(context, currentUser, imageEntity);
*/
         //Api

         Uri picUri = FileUtil.getUri(imageEntity.getFileName(), context);

         if (picUri != null) {
            File file = new File(picUri.getPath());

            if (file.exists()) {
               Log.d(
                  AppConstants.LOG_TAG,
                  "File exists at: " + file.getAbsolutePath()
               );
               imageApiHelper.uploadImage(file, imageEntity);
            } else {
               Log.e(AppConstants.LOG_TAG, "File does not exist.");
            }
         }
         return imageEntity;
      } else {
         Log.e(AppConstants.LOG_TAG, "Invalid image. Format not supported.");
         return null;
      }
   }

   public Uri getImage(User user) {
      String uuid = user.getProfilePictureUuid();
      Uri picUri = null;
      if (uuid != null) {
         //Database
         ImageEntity imageEntity = imageService.getImageByUUID(
            user.getProfilePictureUuid()
         );
         if (imageEntity != null) {
            Log.d(AppConstants.LOG_TAG, "getPicURl: " + imageEntity.getUuid());
            try {
               //Local
               picUri = FileUtil.getUri(imageEntity.getFileName(), context);

               if (picUri == null) {
                  //API
                  Log.d(
                     AppConstants.LOG_TAG,
                     "setImageView: couldn't find the pic locally"
                  );
                  getImageFromAPI(imageEntity.getUuid());
               } else {
                  Log.d(AppConstants.LOG_TAG, "setImageView: " + picUri);
               }
            } catch (Exception e) {
               Log.d(AppConstants.LOG_TAG, "setImageView: " + e);
            }
         } else {
            //API
            getImageFromAPI(user.getProfilePictureUuid());
         }
      } else {
         //TODO getImageFromAPI
      }
      return picUri;
   }

   private void getImageFromAPI(String uuid) {
      ImageApiHelper imageApiHelper = new ImageApiHelper();
      imageApiHelper.getImage(
         uuid,
         responseBody -> {
            boolean writtenToDisk = false;
            try {
               if (responseBody != null) {
                  File file = FileUtil.writeResponseBodyToDisk(
                     responseBody,
                     context,
                     ImageUtil.getImageName(uuid, ImageConstans.TAG_PROFILE)
                  );
               }
               //TODO add image to database
            } catch (IOException e) {
               throw new RuntimeException(e);
            }

            Log.e(
               AppConstants.LOG_TAG,
               "file download was a success? " + writtenToDisk
            );
         }
      );
   }
}
