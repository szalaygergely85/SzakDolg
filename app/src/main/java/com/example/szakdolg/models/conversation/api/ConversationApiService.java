package com.example.szakdolg.models.conversation.api;

import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationApiService {
   @GET("conversation/id/{id}")
   Call<ConversationContent> getConversationAndContentById(
      @Path("id") long id,
      @Header("Authorization") String token
   );

   @POST("conversation/add-conversation")
   Call<Conversation> addConversation(
      @Body Conversation conversation,
      @Header("Authorization") String token
   );

   @POST("conversation/id")
   Call<Long> addNewConversation(
      @Body List<Long> userIds,
      @Header("Authorization") String token
   );

   @GET("conversation/validate-participant")
   Call<
      List<ConversationParticipant>
   > getConversationParticipantAndCompareWithLocal(
      @Query("count") Long count,
      @Header("Authorization") String token
   );

   @GET("conversation/validate")
   Call<List<Conversation>> getConversationAndCompareWithLocal(
      @Query("count") int count,
      @Header("Authorization") String token
   );

   @GET("conversation/{id}")
   Call<Conversation> getConversation(
      @Path("id") long id,
      @Header("Authorization") String token
   );

   @GET("conversation/get-conversations")
   Call<List<Conversation>> getAllConversation(
      @Header("Authorization") String token
   );
}
