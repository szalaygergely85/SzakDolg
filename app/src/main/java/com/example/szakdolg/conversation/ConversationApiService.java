package com.example.szakdolg.conversation;

import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConversationApiService {
   @GET("conversation/id/{id}")
   Call<ConversationContent> getConversationAndContentById(
      @Path("id") long id,
      @Header("Authorization") String token
   );

   @POST("conversation")
   Call<Long> addConversation(
      @Body List<User> participants,
      @Header("Authorization") String token
   );

   @POST("conversation/id")
    Call<Long> addNewConversation(
            @Body List<Long> userIds,
            @Header("Authorization") String token);
}
