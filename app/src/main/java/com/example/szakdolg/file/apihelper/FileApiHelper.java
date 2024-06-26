package com.example.szakdolg.file.apihelper;

import android.net.Uri;
import android.util.Log;

import com.example.szakdolg.file.apiservice.FileApiService;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileApiHelper {

    private FileApiService fileApiService = RetrofitClient.getRetrofitInstance().create(FileApiService.class);


    public void uploadFile(File file) {


    RequestBody requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data"), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
            MultipartBody.Part.createFormData("file", file.getName(), requestFile);



    Call<ResponseBody> uploadImageCall= fileApiService.uploadFile(body);

    uploadImageCall.enqueue(new Callback<ResponseBody>(){
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.e("FileApiHelper", response.toString());
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.e("FileApiHelper", call.toString());

        }
    });

}
}
