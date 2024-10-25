package com.example.szakdolg.db.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

   private static Retrofit retrofit = null;
   private static String BASE_URL = "http://10.0.2.2:8080/api/";

   public static Retrofit getRetrofitInstance() {
      if (retrofit == null) {
         Gson gson = new GsonBuilder().setLenient().create();
         retrofit =
         new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
      }
      return retrofit;
   }
}
