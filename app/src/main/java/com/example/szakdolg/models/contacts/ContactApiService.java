package com.example.szakdolg.models.contacts;

import com.example.szakdolg.models.user.entity.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ContactApiService {

   @POST("contacts/add-contact")
   Call<Contact> addContact(
           @Body Contact contact,
           @Header("Authorization") String authToken
   );

   @DELETE("contacts/delete-contact/{id}")
   Call<Void> deleteContact(
           @Path("id") Long contactId,
           @Header("Authorization") String authToken
   );

   @GET("contacts/get-contact/{id}")
   Call<Contact> getContact(  @Path("id") Long contactId,
                              @Header("Authorization") String authToken);

   @GET("contacts/get-contacts")
   Call<List<Contact>> getContacts(
           @Header("Authorization") String authToken,
           @Query("search") String searchText
   );



   @PUT("contacts/update-contact/{id}")
   Call<Void> updateContact(
           @Body Contact contact,
           @Header("Authorization") String authToken
   );


}
