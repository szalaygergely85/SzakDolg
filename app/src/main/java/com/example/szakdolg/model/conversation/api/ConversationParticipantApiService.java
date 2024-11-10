package com.example.szakdolg.model.conversation.api;

import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConversationParticipantApiService {
   @GET("conversation-participant/get-participants")
   Call<List<ConversationParticipant>> getConversationParticipants(
      @Query("conversationId") Long conversationId,
      @Header("Authorization") String token
   );

   @POST("conversation-participant/add-participants")
   Call<Long> addConversationParticipants(
      @Body List<User> participants,
      @Header("Authorization") String token
   );
}
