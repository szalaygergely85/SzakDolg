package com.example.szakdolg.models.file.api;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.util.FileUtil;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileApiHelper {

   private FileApiService fileApiService = RetrofitClient
      .getRetrofitInstance()
      .create(FileApiService.class);

   public void uploadFile(File file, MessageEntry messageEntry) {
      RequestBody requestFile = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         file
      );

      MultipartBody.Part body = MultipartBody.Part.createFormData(
         "file",
         file.getName(),
         requestFile
      );

      Call<ResponseBody> uploadImageCall = fileApiService.uploadFile(
         body,
         messageEntry
      );

      uploadImageCall.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {
               Log.e("FileApiHelper", response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.e("FileApiHelper", call.toString());
            }
         }
      );
   }

   public void getFile(String imageUrl, Context context, Runnable runnable) {
      Call<ResponseBody> getFileCall = fileApiService.downloadFile(imageUrl);
      getFileCall.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {
               if (response.isSuccessful()) {
                  Log.e(AppConstants.LOG_TAG, "server contacted and has file");

                  boolean writtenToDisk = false;
                  try {
                     File file = FileUtil.writeResponseBodyToDisk(
                        response.body(),
                        context,
                        imageUrl
                     );
                     if (file.exists()) {
                        runnable.run();
                     }
                  } catch (IOException e) {
                     throw new RuntimeException(e);
                  }

                  Log.e(
                     AppConstants.LOG_TAG,
                     "file download was a success? " + writtenToDisk
                  );
               } else {
                  Log.e(AppConstants.LOG_TAG, "server contact failed");
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
         }
      );
   }
}
