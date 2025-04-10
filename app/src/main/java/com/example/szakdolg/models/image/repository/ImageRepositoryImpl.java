package com.example.szakdolg.models.image.repository;

import android.content.Context;
import com.example.szakdolg.models.image.api.ImageApiService;
import com.example.szakdolg.models.image.db.ImageDatabaseUtil;
import com.example.szakdolg.models.image.entity.ImageEntity;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
               ) {}
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
