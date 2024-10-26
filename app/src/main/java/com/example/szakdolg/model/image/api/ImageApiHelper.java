package com.example.szakdolg.model.image.api;

import android.util.Log;

import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.file.api.FileApiService;
import com.example.szakdolg.model.image.entity.ImageEntity;
import com.example.szakdolg.model.message.entity.MessageEntry;

import java.io.File;

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
        RequestBody requestFile = RequestBody.create(file,
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

}
