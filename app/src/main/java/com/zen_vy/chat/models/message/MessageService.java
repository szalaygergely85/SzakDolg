package com.zen_vy.chat.models.message;

import android.content.Context;
import com.zen_vy.chat.activity.base.BaseService;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.repository.MessageRepository;
import com.zen_vy.chat.models.message.repository.MessageRepositoryImpl;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MessageService extends BaseService {

   private MessageRepository messageRepository;

   private MessageDatabaseUtil messageDatabaseUtil;



   public MessageService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.messageRepository = new MessageRepositoryImpl(context, currentUser);
   }

   public void addMessage(
      MessageEntry messageEntry,
      final MessageCallback<MessageEntry> callback
   ) {
       if (messageEntry == null) return;

       String encryptedContentString = null;
       //TODO ENCYPTION
       //messageEntry.setContentEncrypted(EncryptionHelper.encrypt(messageEntry.getContent(), currentUser.getPublicKey()));
       messageEntry.setContentEncrypted(messageEntry.getContent());

       WebSocketService wsService = WebSocketService.getInstance();

       if (wsService != null && wsService.isConnected()) {
           try {
               JSONObject json = new JSONObject();
               json.put("type", messageEntry.getType());
               json.put("senderId", messageEntry.getSenderId());
               json.put("conversationId", messageEntry.getConversationId());
               json.put("uuid", messageEntry.getUuId());
               json.put("timestamp", messageEntry.getTimestamp());
               json.put("contentEncrypted", messageEntry.getContentEncrypted());

               wsService.sendMessage(json.toString());
               messageDatabaseUtil.insertMessageEntry(messageEntry);
               callback.onSuccess(messageEntry); // Assume WebSocket worked
           } catch (JSONException e) {
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
                               callback.onError(new Throwable("Failed to send message via API"));
                           }
                       }

                       @Override
                       public void onFailure(Call<MessageEntry> call, Throwable throwable) {
                           Timber.e(throwable, call.toString());
                           callback.onError(throwable);
                       }
                   }
           );
       }
   }

   public void addMessages(List<MessageEntry> messageEntries,   final MessageCallback<List<MessageEntry>> callback) {
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

   public void getMessagesByConversationId(
      Long conversationId,
      final MessageCallback<List<MessageEntry>> callback
   ) {
      messageRepository.getMessages(
         currentUser.getToken(),
         conversationId,
         new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<List<MessageEntry>> call,
               Response<List<MessageEntry>> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
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

   private boolean _isMessageExists(String messageUuid) {
      List<String> uuidList = messageDatabaseUtil.getAllMessageUuids();
      return uuidList.contains(messageUuid);
   }

   public void updateMessage(MessageEntry entry) {
      messageDatabaseUtil.insertMessageEntry(entry);
   }

   public int getCountByNotReadMsg(Long conversationId) {
       int notReadedCount = messageDatabaseUtil.getUnreadMessageCountByConversationId(
               conversationId
       );
       Timber.i("Getting getCountByNotReadMsg and it is: " + notReadedCount);
      return notReadedCount;
   }

   public void setMessagesAsReadByConversationId(Long conversationId) {
      messageDatabaseUtil.setMessagesAsReadByConversationId(conversationId);
   }

   public interface MessageCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
