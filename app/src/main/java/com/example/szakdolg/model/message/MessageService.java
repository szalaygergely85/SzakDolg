package com.example.szakdolg.model.message;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.List;

public class MessageService extends BaseService {

   private MessageDatabaseUtil messageDatabaseUtil;

   public MessageService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
   }

   public void addMessage(MessageEntry messageEntry) {
      if (messageEntry != null) {
         messageDatabaseUtil.insertMessageEntry(messageEntry);
      }
   }

   public void addMessages(List<MessageEntry> messageEntries) {
      for (MessageEntry messageEntry : messageEntries) {
         if (!_isMessageExists(messageEntry.getMessageId())) {
            addMessage(messageEntry);
         }
      }
   }

   public MessageEntry getLatestMessageEntry(Long conversationId) {
      MessageEntry messageEntry = messageDatabaseUtil.getLatestMessageEntry(
         conversationId
      );
      return messageEntry;
   }

   public List<MessageEntry> getMessagesByConversationId(Long conversationId) {
      List<MessageEntry> messageEntries =
         messageDatabaseUtil.getAllMessageEntriesByConversationId(
            conversationId
         );
      return messageEntries;
   }

   private boolean _isMessageExists(Long messageId) {
      List<Long> idList = messageDatabaseUtil.getAllMessageIds();
      return idList.contains(messageId);
   }
}
