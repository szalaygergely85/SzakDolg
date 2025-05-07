package com.zen_vy.chat.models.message.repository;

import com.zen_vy.chat.models.message.entity.MessageEntry;
import java.util.List;
import retrofit2.Callback;

public interface MessageRepository {
   public void addMessage(
      MessageEntry messageEntry,
      Callback<MessageEntry> callback
   );

   public void addMessages(
      List<MessageEntry> messageEntries,
      Callback<List<MessageEntry>> callback
   );

   public void getLatestMessage(
      String token,
      Long conversationId,
      Callback<MessageEntry> callback
   );

   public void getMessages(
      String token,
      Long conversationId,
      Callback<List<MessageEntry>> callback
   );
}
