package com.example.szakdolg.models.message;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.message.repository.MessageRepository;
import com.example.szakdolg.models.message.repository.MessageRepositoryImpl;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageService extends BaseService {

   private MessageRepository messageRepository;

   private MessageDatabaseUtil messageDatabaseUtil;

   public MessageService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.messageRepository = new MessageRepositoryImpl(context, currentUser);
   }

   public void addMessage(MessageEntry messageEntry, final MessageCallback<MessageEntry> callback) {
      if (messageEntry != null) {
        messageRepository.addMessage(messageEntry, new Callback<MessageEntry>() {
           @Override
           public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
              if (response.isSuccessful()) {
                 callback.onSuccess(response.body());
              } else {
                 callback.onError(new Throwable("Failed to update contact"));
              }
           }

           @Override
           public void onFailure(Call<MessageEntry> call, Throwable throwable) {

           }
        });
      }

   }

   public void addMessages(List<MessageEntry> messageEntries) {
      messageRepository.addMessages(messageEntries, new Callback<MessageEntry>() {
         @Override
         public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {

         }

         @Override
         public void onFailure(Call<MessageEntry> call, Throwable throwable) {

         }
      });
   }

   public void getLatestMessageEntry(Long conversationId, final MessageCallback<MessageEntry> callback) {
      messageRepository.getLatestMessage(currentUser.getToken(), conversationId, new Callback<MessageEntry>() {
         @Override
         public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
            if (response.isSuccessful()) {
               callback.onSuccess(response.body());
            } else {
               callback.onError(new Throwable("Failed to update contact"));
            }
         }

         @Override
         public void onFailure(Call<MessageEntry> call, Throwable throwable) {
            callback.onError(throwable);
         }
      });
   }


   public void getMessagesByConversationId(Long conversationId,  final MessageCallback<List<MessageEntry>> callback) {
      messageRepository.getMessages(currentUser.getToken(), conversationId, new Callback<List<MessageEntry>>() {
         @Override
         public void onResponse(Call<List<MessageEntry>> call, Response<List<MessageEntry>> response) {
            if (response.isSuccessful()) {
               callback.onSuccess(response.body());
            } else {
               callback.onError(new Throwable("Failed to update contact"));
            }
         }

         @Override
         public void onFailure(Call<List<MessageEntry>> call, Throwable throwable) {
            callback.onError(throwable);
         }
      });
   }

   private boolean _isMessageExists(String messageUuid) {
      List<String> uuidList = messageDatabaseUtil.getAllMessageUuids();
      return uuidList.contains(messageUuid);
   }

   public void updateMessage(MessageEntry entry) {
  messageDatabaseUtil.insertMessageEntry(entry);
   }

   public int getCountByNotReadMsg(Long conversationId) {
      return messageDatabaseUtil.getUnreadMessageCountByConversationId(
         conversationId
      );
   }

   public void setMessagesAsReadByConversationId(Long conversationId) {
      messageDatabaseUtil.setMessagesAsReadByConversationId(conversationId);
   }


   public interface MessageCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
