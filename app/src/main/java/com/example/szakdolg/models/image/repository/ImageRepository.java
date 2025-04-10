package com.example.szakdolg.models.image.repository;

import com.example.szakdolg.models.image.entity.ImageEntity;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
