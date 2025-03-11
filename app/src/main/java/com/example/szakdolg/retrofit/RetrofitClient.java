package com.example.szakdolg.retrofit;

import com.example.szakdolg.constans.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

   private static volatile Retrofit retrofit = null;
   private static String BASE_URL = "http://10.0.2.2:8080/api/";

   private RetrofitClient() {} // Private constructor to prevent instantiation

   public static Retrofit getRetrofitInstance() {
      if (retrofit == null) {  // First check (no lock)
         synchronized (RetrofitClient.class) {  // Synchronization block
            if (retrofit == null) {  // Second check (inside lock)
               Gson gson = new GsonBuilder().setLenient().create();
               retrofit = new Retrofit.Builder()
                       .baseUrl(BASE_URL)
                       .addConverterFactory(GsonConverterFactory.create(gson))
                       .build();
            }
         }
      }
      return retrofit;
   }
}
