package com.zen_vy.chat.models.conversation.repository;

import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import java.util.List;
import retrofit2.Callback;

public interface ConversationRepository {
   void addConversation(
      Conversation conversation,
      String token,
      Callback<Conversation> callback
   );

   void addConversationByUserId(
      List<Long> userIds,
      String token,
      Callback<ConversationDTO> callback
   );

   void getConversation(
      Long id,
      String token,
      Callback<ConversationDTO> callback
   );

   void getAllConversation(
      String token,
      Callback<List<ConversationDTO>> callback
   );

   void deleteConversation(
      Long conversationId,
      String token,
      Callback<Void> callback
   );
}
