package com.example.szakdolg.file.apihelper;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.file.apiservice.FileApiService;
import com.example.szakdolg.message.MessageEntry;
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

   private static final String TAG = "FileApiHelper";

   private FileApiService fileApiService = RetrofitClient
      .getRetrofitInstance()
      .create(FileApiService.class);

   public void uploadFile(File file, MessageEntry messageEntry) {
      RequestBody requestFile = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         file
      );

      // MultipartBody.Part is used to send also the actual file name
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
                  Log.e(TAG, "server contacted and has file");

                  boolean writtenToDisk = false;
                  try {
                     writtenToDisk =
                     FileUtil.writeResponseBodyToDisk(
                        response.body(),
                        context,
                        imageUrl
                     );
                  } catch (IOException e) {
                     throw new RuntimeException(e);
                  }

                  Log.e(TAG, "file download was a success? " + writtenToDisk);
                  if (writtenToDisk) {
                     runnable.run();
                  }
               } else {
                  Log.e(TAG, "server contact failed");
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
         }
      );
   }
}
