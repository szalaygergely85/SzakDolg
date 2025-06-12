package com.zen_vy.chat.models.image.repository;

import android.content.Context;
import com.zen_vy.chat.models.image.api.ImageApiService;
import com.zen_vy.chat.models.image.db.ImageDatabaseUtil;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.retrofit.RetrofitClient;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ImageRepositoryImpl implements ImageRepository {

   private final Context context;
   private final User currentUser;
   private final ImageApiService imageApiService;
   private ImageDatabaseUtil imageDatabaseUtil;

   public ImageRepositoryImpl(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.imageApiService =
      RetrofitClient.getRetrofitInstance().create(ImageApiService.class);
      imageDatabaseUtil = new ImageDatabaseUtil(context, currentUser);
   }

   @Override
   public void uploadImage(
      MultipartBody.Part filePart,
      ImageEntity imageEntity,
      String token,
      Callback<ResponseBody> callback
   ) {
      imageApiService
         .uploadImage(filePart, imageEntity, token)
         .enqueue(
            new Callback<ResponseBody>() {
               @Override
               public void onResponse(
                  Call<ResponseBody> call,
                  Response<ResponseBody> response
               ) {
                  if (response.body() != null) {
                     imageDatabaseUtil.insertImageEntity(imageEntity);
                  }
                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(
                  Call<ResponseBody> call,
                  Throwable throwable
               ) {
                  Timber.w(throwable, call.toString());
               }
            }
         );
   }

   @Override
   public void downloadImage(
      String uuid,
      String token,
      Callback<ResponseBody> callback
   ) {}
}
