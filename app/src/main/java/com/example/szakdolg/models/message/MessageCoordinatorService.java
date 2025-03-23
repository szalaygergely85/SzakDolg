package com.example.szakdolg.models.message;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.message.api.MessageApiHelper;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.websocket.WebSocketService;
import java.util.List;

public class MessageCoordinatorService extends BaseService {

   private final MessageService messageService;

   private final MessageApiHelper messageApiHelper;

   private final WebSocketService webSocketService =
      WebSocketService.getInstance();

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
            messageService::addMessages
         );
         return null;
      }
   }

   public void sendMessage(MessageEntry messageEntry) {
      messageService.addMessage(messageEntry);
      if (messageEntry.getContentEncrypted() != null) {
         webSocketService.sendMessage(messageEntry.getJSON());

         messageApiHelper.addMessage(
            messageEntry,
            entry -> {
               entry.setUploaded(true);
               messageService.updateMessage(entry);
            }
         );
      }
   }

   public MessageEntry saveMessage(MessageEntry messageEntry) {
      String encryptedContent = messageEntry.getContentEncrypted();
      String content = messageEntry.getContent();

      if (encryptedContent != null && content == null) {
         content =
         EncryptionHelper.decrypt(
            encryptedContent,
            KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
         );
         messageEntry.setContent(content);
      }

      return messageService.addMessage(messageEntry);
   }

   public int getCountByNotReadMsg(Long conversationId) {
      return messageService.getCountByNotReadMsg(conversationId);
   }

   public void setMessagesAsReadByConversationId(Long conversationId) {
      messageService.setMessagesAsReadByConversationId(conversationId);
   }
}
