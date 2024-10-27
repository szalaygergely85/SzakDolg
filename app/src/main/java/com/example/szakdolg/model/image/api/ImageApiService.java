package com.example.szakdolg.model.image.api;

import com.example.szakdolg.model.image.entity.ImageEntity;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageApiService {
   @Multipart
   @POST("image/upload")
   Call<ResponseBody> uploadImage(
      @Part MultipartBody.Part filePart,
      @Part("imageEntry") ImageEntity imageEntity
   );

   @GET("image/{uuid}")
   Call<ResponseBody> downloadFile(@Path("uuid") String uuid);
}
