package com.zen_vy.chat.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zen_vy.chat.constans.AppConstants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

   private static volatile Retrofit retrofit = null;
   private static String BASE_URL = AppConstants.API_URL;

   private RetrofitClient() {} // Private constructor to prevent instantiation

   public static Retrofit getRetrofitInstance() {
      if (retrofit == null) {
         synchronized (RetrofitClient.class) {
            if (retrofit == null) {
               // Set up logging interceptor
               HttpLoggingInterceptor loggingInterceptor =
                  new HttpLoggingInterceptor();
               loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

               OkHttpClient client = new OkHttpClient.Builder()
                  .addInterceptor(loggingInterceptor)
                  .build();

               Gson gson = new GsonBuilder().setLenient().create();

               retrofit =
               new Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .client(client) // Add the client with logging
                  .addConverterFactory(GsonConverterFactory.create(gson))
                  .build();
            }
         }
      }
      return retrofit;
   }
}
