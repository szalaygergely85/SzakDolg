package com.example.szakdolg.contacts;

import com.example.szakdolg.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ContactsApiService {
   @GET("contacts/{token}/byToken")
   Call<List<User>> getConversation(@Path("token") String token);

   @GET("contacts/{search}/search")
   Call<List<User>> searchContacts(@Path("search") String text);

   @FormUrlEncoded
   @POST("contacts")
   Call<Boolean> addContact(
      @Field("ownerId") Long ownerId,
      @Field("contactId") Long contactId
   );
}
