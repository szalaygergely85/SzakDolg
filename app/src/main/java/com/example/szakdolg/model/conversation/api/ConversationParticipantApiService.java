package com.example.szakdolg.model.conversation.api;

import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ConversationParticipantApiService {
   @GET("conversation-participant/conversation/{id}")
   Call<List<ConversationParticipant>> getConversationParticipants(
      @Path("id") long id,
      @Header("Authorization") String token
   );
}
