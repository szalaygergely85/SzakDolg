package com.example.szakdolg.model.message;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.List;

public class MessageCoordinatorService extends BaseService {

   private MessageService messageService;

   private MessageApiHelper messageApiHelper;

   public MessageCoordinatorService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageService = new MessageService(context, currentUser);
      this.messageApiHelper = new MessageApiHelper(context, currentUser);
   }

   public List<MessageEntry> getMessagesByConversationId(Long conversationId) {
      List<MessageEntry> messages = messageService.getMessagesByConversationId(
         conversationId
      );
      if (messages != null) {
         return messages;
      } else {
         messageApiHelper.getMessages(
            conversationId,
            messageEntries -> {
               messageService.addMessages(messageEntries);
            }
         );
         return null;
      }
   }

   public MessageEntry getLatestMessageEntry(Long conversationId) {
      MessageEntry messageEntry = messageService.getLatestMessageEntry(
         conversationId
      );
      if (messageEntry != null) {
         return messageEntry;
      } else {
         messageApiHelper.getMessages(
            conversationId,
            messageEntries -> {
               messageService.addMessages(messageEntries);
            }
         );
         return null;
      }
   }

   public void addMessage(MessageEntry messageEntry) {
      messageService.addMessage(messageEntry);
      messageApiHelper.addMessage(messageEntry, null);
   }
}
