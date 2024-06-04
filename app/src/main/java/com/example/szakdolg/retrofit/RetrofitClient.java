package com.example.szakdolg.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static String BASE_URL ="http://10.0.2.2:8080/api/";

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitInstanceWithCustomDeserializers(Object... deserializers){
        if (retrofit == null){
            GsonBuilder gsonBuilder = new GsonBuilder();
            for (Object deserializer : deserializers) {
                gsonBuilder.registerTypeAdapter(deserializer.getClass(), deserializer);
            }
            Gson gson = gsonBuilder.create();

            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
