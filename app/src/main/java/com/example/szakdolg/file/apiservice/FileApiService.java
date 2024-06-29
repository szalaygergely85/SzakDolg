package com.example.szakdolg.file.apiservice;

import com.example.szakdolg.message.MessageEntry;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FileApiService {
   @Multipart
   @POST("files/upload") // Replace with your actual endpoint
   Call<ResponseBody> uploadFile(
      @Part MultipartBody.Part filePart,
      @Part("messageEntry") MessageEntry messageEntry
   );

   @GET("files/{filename}")
   Call<ResponseBody> downloadFile(@Path("filename") String filename);
}
