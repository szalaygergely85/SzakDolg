package com.example.szakdolg.model.message.api;

import com.example.szakdolg.model.message.entity.MessageEntry;
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
   Call<MessageEntry> sendMessage(
      @Body MessageEntry message,
      @Header("Authorization") String token
   );

   @GET("message/new-message")
   Call<List<MessageEntry>> getNewMessages(
      @Header("Authorization") String token
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
