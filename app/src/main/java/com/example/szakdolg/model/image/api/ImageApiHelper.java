package com.example.szakdolg.model.image.api;

import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.image.entity.ImageEntity;
import java.io.File;
import java.util.function.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageApiHelper {

   private ImageApiService imageApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ImageApiService.class);

   public void uploadImage(File file, ImageEntity imageEntity) {
      RequestBody requestFile = RequestBody.create(
         file,
         MediaType.get("multipart/form-data")
      );

      MultipartBody.Part body = MultipartBody.Part.createFormData(
         "image",
         file.getName(),
         requestFile
      );

      Call<ResponseBody> uploadImageCall = imageApiService.uploadImage(
         body,
         imageEntity
      );

      uploadImageCall.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {
               Log.d(AppConstants.LOG_TAG, response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.e(AppConstants.LOG_TAG, call.toString());
            }
         }
      );
   }

   public void getImage(String uuid, Consumer<ResponseBody> onFileDownloaded) {
      Call<ResponseBody> getImageCall = imageApiService.downloadFile(uuid);
      getImageCall.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {
               if (response.isSuccessful() && response.body() != null) {
                  Log.e(AppConstants.LOG_TAG, "server contacted and has file");
                  onFileDownloaded.accept(response.body());
               } else {
                  Log.e(
                     AppConstants.LOG_TAG,
                     "server contact failed or empty body"
                  );
                  onFileDownloaded.accept(null);
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.e(AppConstants.LOG_TAG, "file download failed", t);
               onFileDownloaded.accept(null);
            }
         }
      );
   }
}
