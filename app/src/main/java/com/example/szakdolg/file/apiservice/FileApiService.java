package com.example.szakdolg.file.apiservice;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileApiService {

    @Multipart
    @POST("files/upload") // Replace with your actual endpoint
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part filePart);
}
