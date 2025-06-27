package com.zen_vy.chat.models.message.api;

import com.zen_vy.chat.models.message.entity.MessageEntry;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageApiService {
   @DELETE("message/remove/{uuid}")
   Call<Void> deleteMessageByUuid(
      @Header("Authorization") String token,
      @Path("uuid") String uuid
   );

   @POST("message/add-message")
   Call<MessageEntry> addMessage(
      @Body MessageEntry message,
      @Header("Authorization") String token
   );

   @POST("message/add-messages")
   Call<List<MessageEntry>> addMessages(
      @Body List<MessageEntry> messages,
      @Header("Authorization") String token
   );


   @GET("message/new-message")
   Call<List<MessageEntry>> getNewMessages(
      @Header("Authorization") String token
   );

   @GET("message/get-messages")
   Call<List<MessageEntry>> getMessages(
      @Header("Authorization") String token,
      @Query("conversationId") Long conversationId
   );

   @GET("message/get-latest-message")
   Call<MessageEntry> getLatestMessage(
      @Header("Authorization") String token,
      @Query("conversationId") Long conversationId
   );

   @GET("message/get-messages/not-delivered")
   Call<List<MessageEntry>> getNotDeliveredMessages(
           @Header("Authorization") String token
   );

   @PATCH("message/mark-as-downloaded")
   Call<Void> markMessagesAsDownloaded(@Header("Authorization") String token, @Body List<String> messageUuids);
}
