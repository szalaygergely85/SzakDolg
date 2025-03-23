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

   public int getCountByNotReadMsg(Long conversationId) {
      return messageService.getCountByNotReadMsg(conversationId);
   }

   public void setMessagesAsReadByConversationId(Long conversationId) {
      messageService.setMessagesAsReadByConversationId(conversationId);
   }
}
