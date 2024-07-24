package com.example.szakdolg.message;

import com.example.szakdolg.messageboard.DTO.MessageBoard;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageApiService {
   @GET("message/messageboardentries")
   Call<ArrayList<MessageBoard>> getLatestMessages(
      @Header("Authorization") String token
   );

   @POST("message")
   Call<MessageEntry> sendMessage(
      @Body MessageEntry message,
      @Header("Authorization") String token
   );

   @GET("message/byconversationid/{id}")
   Call<ArrayList<MessageEntry>> getConversationMessages(
      @Path("id") long conversationId,
      @Header("Authorization") String token
   );

   @Deprecated
   @GET("message/new-message")
   Call<ArrayList<MessageBoard>> getConversationWithNewMessage(
      @Header("Authorization") String token
   );
}
