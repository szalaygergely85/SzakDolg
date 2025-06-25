package com.zen_vy.chat.models.conversation.api;

import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.entity.ConversationParticipant;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationApiService {
   @GET("conversation/get-conversation/{id}")
   Call<ConversationDTO> getConversationAndContentById(
      @Path("id") long id,
      @Header("Authorization") String token
   );

   @POST("conversation/add-conversation/conversation")
   Call<Conversation> addConversation(
      @Body Conversation conversation,
      @Header("Authorization") String token
   );

   @POST("conversation/add-conversation/user-ids")
   Call<ConversationDTO> addConversationByUserId(
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

   @GET("conversation/get-conversation/{id}")
   Call<ConversationDTO> getConversation(
      @Path("id") long id,
      @Header("Authorization") String token
   );

   @GET("conversation/get-conversations")
   Call<List<ConversationDTO>> getAllConversation(
      @Header("Authorization") String token
   );

   @DELETE("conversation/remove-conversation/{conversationId}")
   Call<Void> deleteConversation(
           @Path("conversationId") Long conversationId,
           @Header("Authorization") String token
   );
}
