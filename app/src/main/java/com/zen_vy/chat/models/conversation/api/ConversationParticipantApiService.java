package com.zen_vy.chat.models.conversation.api;

import com.zen_vy.chat.models.conversation.entity.ConversationParticipant;
import java.util.List;
import okhttp3.ResponseBody;
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
   Call<ResponseBody> addConversationParticipants(
      @Body List<ConversationParticipant> participants,
      @Header("Authorization") String token
   );
}
