package com.example.szakdolg.models.image.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.image.ImageLocalHelper;
import com.example.szakdolg.models.image.constans.ImageConstans;
import com.example.szakdolg.models.image.db.ImageDatabaseUtil;
import com.example.szakdolg.models.image.entity.ImageEntity;
import com.example.szakdolg.models.image.repository.ImageRepository;
import com.example.szakdolg.models.image.repository.ImageRepositoryImpl;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserService;
import com.example.szakdolg.util.FileUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageService {

   private Context context;

   private User currentUser;

   private ImageDatabaseUtil imageDatabaseUtil;

   private ImageRepository imageRepository;

   public ImageService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      imageDatabaseUtil = new ImageDatabaseUtil(context, currentUser);
      imageRepository = new ImageRepositoryImpl(context, currentUser);
   }

   public void uploadImage(File file, ImageEntity imageEntity) {}

   public void addPicture(
      Uri imageUri,
      Long userId,
      String tags,
      final ImageService.ImageCallback callback
   ) {
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
         ImageLocalHelper imageLocalHelper = new ImageLocalHelper();
         imageLocalHelper.saveImageToDisk(
            context,
            currentUser,
            imageEntity,
            new ImageLocalHelper.ImageSaveCallback() {
               @Override
               public void onImageSaved(ImageEntity updatedImage) {
                  Uri picUri = FileUtil.getUri(
                     imageEntity.getFileName(),
                     context
                  );

                  if (picUri != null) {
                     File file = new File(picUri.getPath());

                     if (file.exists()) {
                        Log.d(
                           AppConstants.LOG_TAG,
                           "File exists at: " + file.getAbsolutePath()
                        );

                        RequestBody requestFile = RequestBody.create(
                           file,
                           MediaType.get("multipart/form-data")
                        );

                        MultipartBody.Part body =
                           MultipartBody.Part.createFormData(
                              "image",
                              file.getName(),
                              requestFile
                           );

                        imageRepository.uploadImage(
                           body,
                           imageEntity,
                           currentUser.getToken(),
                           new Callback<ResponseBody>() {
                              @Override
                              public void onResponse(
                                 Call<ResponseBody> call,
                                 Response<ResponseBody> response
                              ) {
                                 if (response.isSuccessful()) {
                                    callback.onSuccess(response.body());
                                 } else {
                                    callback.onError(
                                       new Throwable(
                                          "Failed to update Conversation"
                                       )
                                    );
                                 }
                              }

                              @Override
                              public void onFailure(
                                 Call<ResponseBody> call,
                                 Throwable throwable
                              ) {}
                           }
                        );
                     } else {
                        Log.e(AppConstants.LOG_TAG, "File does not exist.");
                     }
                  }
               }

               @Override
               public void onError(Exception e) {}
            }
         );
      }
   }


   public ImageEntity getImageByUUID(String uuid) {
      return imageDatabaseUtil.getImageEntityByUuid(uuid);
   }

   public void updateImage(ImageEntity imageEntity) {}

   public interface ImageCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
