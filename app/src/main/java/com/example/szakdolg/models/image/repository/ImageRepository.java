package com.example.szakdolg.models.image.repository;

import com.example.szakdolg.models.image.entity.ImageEntity;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public interface ImageRepository {
   void uploadImage(
      MultipartBody.Part filePart,
      ImageEntity imageEntity,
      String token,
      Callback<ResponseBody> callback
   );

   void downloadImage(
      String uuid,
      String token,
      Callback<ResponseBody> callback
   );
}
