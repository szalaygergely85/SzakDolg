package com.zen_vy.chat.testhelpers.services;

import android.content.Context;
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;

public class TestConversationService extends ConversationService {

   private List<ConversationDTO> mockList;

   public TestConversationService(
      Context context,
      User currentUser,
      List<ConversationDTO> mockList
   ) {
      super(context, currentUser);
      this.mockList = mockList;
   }

   @Override
   public void getAllConversations(
      ConversationCallback<List<ConversationDTO>> callback
   ) {
      if (!mockList.isEmpty()) {
         callback.onSuccess(mockList);
      }
   }
}
