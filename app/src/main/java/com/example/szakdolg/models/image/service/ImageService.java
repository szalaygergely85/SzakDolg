package com.example.szakdolg.models.image.service;

import android.content.Context;
import com.example.szakdolg.models.image.db.ImageDatabaseUtil;
import com.example.szakdolg.models.image.entity.ImageEntity;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserService;

public class ImageService {

   private Context context;

   private User currentUser;

   private ImageDatabaseUtil imageDatabaseUtil;

   public ImageService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      imageDatabaseUtil = new ImageDatabaseUtil(context, currentUser);
   }

   public void addImage(ImageEntity imageEntity) {
      imageDatabaseUtil.insertImageEntity(imageEntity);
   }

   public ImageEntity getImageByUserId(Long userId) {
      UserService userService = new UserService(context);
      User user = userService.getUserByUserId(userId, currentUser);
      ImageEntity imageEntity = imageDatabaseUtil.getImageEntityByUuid(
         user.getProfilePictureUuid()
      );
      return imageEntity;
   }

   public ImageEntity getImageByUUID(String uuid) {
      return imageDatabaseUtil.getImageEntityByUuid(uuid);
   }

   public void updateImage(ImageEntity imageEntity) {}
}
