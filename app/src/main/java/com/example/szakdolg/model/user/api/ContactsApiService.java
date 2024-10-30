package com.example.szakdolg.model.user.api;

import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ContactsApiService {
   @GET("contacts")
   Call<List<User>> getContacts(@Header("Authorization") String authToken);

   @GET("contacts/{search}/search")
   Call<List<User>> searchContacts(
      @Path("search") String text,
      @Header("Authorization") String authToken
   );

   @FormUrlEncoded
   @POST("contacts")
   Call<User> addContact(
      @Field("ownerId") Long ownerId,
      @Field("contactId") Long contactId,
      @Header("Authorization") String authToken
   );

   @GET("contacts/validate")
   Call<ArrayList<User>> getContactsAndCompareWithLocal(
      @Query("count") int count,
      @Header("Authorization") String token
   );
}
