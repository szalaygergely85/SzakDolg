package com.zen_vy.chat.models.image.api;

import android.util.Log;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.retrofit.RetrofitClient;
import java.io.File;
import java.util.function.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageApiHelper {

   private ImageApiService imageApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ImageApiService.class);

   public void uploadImage(File file, ImageEntity imageEntity) {
      /*
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

		*/
   }

   public void getImage(String uuid, String token,  Consumer<ResponseBody> onFileDownloaded) {
      Call<ResponseBody> getImageCall = imageApiService.downloadFile(uuid, token);
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
