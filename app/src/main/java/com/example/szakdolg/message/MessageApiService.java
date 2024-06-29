package com.example.szakdolg.message;

import com.example.szakdolg.DTO.MessageBoard;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

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

   @GET("message/byconversationid/")
   Call<ArrayList<MessageEntry>> getConversationMessages(
      @Field("id") long conversationId,
      @Header("Authorization") String token
   );
}
