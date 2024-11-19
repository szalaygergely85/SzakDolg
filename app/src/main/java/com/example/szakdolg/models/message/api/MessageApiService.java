package com.example.szakdolg.models.message.api;

import com.example.szakdolg.models.message.entity.MessageEntry;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageApiService {
   @POST("message")
   Call<MessageEntry> addMessage(
      @Body MessageEntry message,
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

   @Deprecated
   @GET("message/byconversationid/{id}")
   Call<ArrayList<MessageEntry>> getConversationMessages(
      @Path("id") long conversationId,
      @Header("Authorization") String token
   );

   @GET("message/validate")
   Call<ArrayList<MessageEntry>> getMessagesAndCompareWithLocal(
      @Query("count") Long count,
      @Header("Authorization") String token
   );

   @PATCH("message/mark-as-downloaded")
   Call<String> markMessagesAsDownloaded(@Body List<Long> messageIds);
}
