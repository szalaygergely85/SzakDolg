package com.zen_vy.chat.models.message;

import android.content.Context;

import com.zen_vy.chat.DTO.MessageDTO;
import com.zen_vy.chat.activity.base.BaseService;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.entity.MessageStatus;
import com.zen_vy.chat.models.message.entity.MessageStatusType;
import com.zen_vy.chat.models.message.repository.MessageRepository;
import com.zen_vy.chat.models.message.repository.MessageRepositoryImpl;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.websocket.MessageFactory;
import com.zen_vy.chat.websocket.WebSocketService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MessageService extends BaseService {

   private MessageRepository messageRepository;

   private MessageDatabaseUtil messageDatabaseUtil;

   private MessageStatusDatabaseUtil messageStatusDatabaseUtil;

   public MessageService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.messageRepository = new MessageRepositoryImpl(context, currentUser);
      this.messageStatusDatabaseUtil = new MessageStatusDatabaseUtil(context, currentUser);
   }

   public void addMessage(
      MessageEntry messageEntry,
      final MessageCallback<MessageEntry> callback
   ) {
      if (messageEntry == null) return;

      String encryptedContentString = null;
      //TODO ENCYPTION
      //messageEntry.setContentEncrypted(EncryptionHelper.encrypt(messageEntry.getContent(), currentUser.getPublicKey()));

      WebSocketService wsService = WebSocketService.getInstance();

      if (wsService != null && wsService.isConnected()) {
         try {
            String messageString = MessageFactory.MessageEntryMessage(messageEntry);

            wsService.sendMessage(messageString);
            messageDatabaseUtil.insertMessageEntry(messageEntry);
            callback.onSuccess(messageEntry); // Assume WebSocket worked
         } catch (Exception e) {
            Timber.e(e);
            callback.onError(e);
         }
      } else {
         Timber.w("Web socket is not available, sending message through api");
         // WebSocket not available – fallback to API
         messageRepository.addMessage(
            messageEntry,
            new Callback<MessageEntry>() {
               @Override
               public void onResponse(
                  Call<MessageEntry> call,
                  Response<MessageEntry> response
               ) {
                  if (response.isSuccessful()) {
                     callback.onSuccess(response.body());
                  } else {
                     Timber.e("Failed to send message by Api");
                     callback.onError(
                        new Throwable("Failed to send message via API")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<MessageEntry> call,
                  Throwable throwable
               ) {
                  Timber.e(throwable, call.toString());
                  callback.onError(throwable);
               }
            }
         );
      }
   }

   public void addMessages(
      List<MessageEntry> messageEntries,
      final MessageCallback<List<MessageEntry>> callback
   ) {
      messageRepository.addMessages(
         messageEntries,
         new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<List<MessageEntry>> call,
               Response<List<MessageEntry>> response
            ) {
               callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(
               Call<List<MessageEntry>> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void getLatestMessageEntry(
      Long conversationId,
      final MessageCallback<MessageEntry> callback
   ) {
      messageRepository.getLatestMessage(
         currentUser.getToken(),
         conversationId,
         new Callback<MessageEntry>() {
            @Override
            public void onResponse(
               Call<MessageEntry> call,
               Response<MessageEntry> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(
               Call<MessageEntry> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void getPendingMessages() {
      messageRepository.getPendingMessages(
         currentUser.getToken(),
         new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<List<MessageEntry>> call,
               Response<List<MessageEntry>> response
            ) {
               if (response.isSuccessful()) {
                  List<MessageEntry> messages = response.body();
                  if (!messages.isEmpty()) {
                     List<String> uuids = new ArrayList<>();
                     for (MessageEntry messageEntry : messages) {
                        messageDatabaseUtil.insertMessageEntry(messageEntry);
                        uuids.add(messageEntry.getUuid());
                     }
                     messageRepository.setMessageDownloaded(
                        currentUser.getToken(),
                        uuids,
                        new Callback<Void>() {
                           @Override
                           public void onResponse(
                              Call<Void> call,
                              Response<Void> response
                           ) {
                              Timber.i(
                                 "%s are set to downloaded",
                                 uuids.toString()
                              );
                           }

                           @Override
                           public void onFailure(
                              Call<Void> call,
                              Throwable throwable
                           ) {}
                        }
                     );
                  }
               }
            }

            @Override
            public void onFailure(
               Call<List<MessageEntry>> call,
               Throwable throwable
            ) {}
         }
      );
   }

   public void getMessagesByConversationId(
      Long conversationId,
      final MessageCallback<List<MessageDTO>> callback
   ) {
      getPendingMessages();

      List<MessageEntry> localMessages =
         messageDatabaseUtil.getAllMessageEntriesByConversationId(
            conversationId
         );
      // if (localMessages.isEmpty()) {
      messageRepository.getMessages(
         currentUser.getToken(),
         conversationId,
         new Callback<List<MessageDTO>>() {
            @Override
            public void onResponse(
               Call<List<MessageDTO>> call,
               Response<List<MessageDTO>> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(
               Call<List<MessageDTO>> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
      //}
   }

   private boolean _isMessageExists(String messageUuid) {
      List<String> uuidList = messageDatabaseUtil.getAllMessageUuids();
      return uuidList.contains(messageUuid);
   }

   public void updateMessage(MessageEntry entry) {
      messageDatabaseUtil.insertMessageEntry(entry);
   }

   public int getCountByNotReadMsg(Long conversationId) {
      int notReadCount =
         messageDatabaseUtil.getUnreadMessageCountByConversationId(
            conversationId,
            currentUser.getUserId()
         );
      Timber.i("Getting getCountByNotReadMsg and it is: " + notReadCount);
      return notReadCount;
   }

   public void setMessagesAsRead(List<MessageDTO> messages) {
if (messages.size()>0) {
    messageDatabaseUtil.setMessagesAsReadByConversationId(messages.get(0).getMessage().getMessageId(), currentUser.getUserId());
    WebSocketService service = WebSocketService.getInstance();
    for (MessageDTO messageDTO : messages) {
        MessageEntry messageEntry = messageDTO.getMessage();
        MessageStatus messageStatus = messageDTO.getStatus();
        Map<Long, MessageStatusType> status = messageStatus.getUserStatuses();
        if (status.get(currentUser.getUserId()) != MessageStatusType.READ) {
            service.sendMessage(MessageFactory.messageStatusUpdate(messageEntry, currentUser.getUserId(), MessageStatusType.READ));
        }
    }
}
   }

   public void deleteMessage(
      MessageEntry messageEntry,
      final MessageCallback<List<MessageEntry>> callback
   ) {
      messageRepository.deleteMessage(
         currentUser.getToken(),
         messageEntry.getUuid(),
         new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {}
         }
      );
   }

    public String getLatestReadMessage(Long conversationId) {
       return messageDatabaseUtil.getLastReadMessageInConversation(conversationId, currentUser.getUserId());
    }

    public interface MessageCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
