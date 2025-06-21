package com.zen_vy.chat.models.message.repository;

import android.content.Context;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.message.MessageDatabaseUtil;
import com.zen_vy.chat.models.message.api.MessageApiService;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.retrofit.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageRepositoryImpl implements MessageRepository {

   private final Context context;
   private final User currentUser;
   private final MessageDatabaseUtil messageDatabaseUtil;
   private final MessageApiService messageApiService;

   public MessageRepositoryImpl(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.messageApiService =
      RetrofitClient.getRetrofitInstance().create(MessageApiService.class);
   }

   @Override
   public void getLatestMessage(
      String token,
      Long conversationId,
      Callback<MessageEntry> callback
   ) {
      MessageEntry localMessage = messageDatabaseUtil.getLatestMessageEntry(
         conversationId
      );
      if (localMessage != null) {
         if (
            System.currentTimeMillis() - localMessage.getTimestamp() >
            AppConstants.MESSAGE_SYNC_TIME
         ) {
            callback.onResponse(null, Response.success(localMessage));
            return;
         }
      }
      messageApiService
         .getLatestMessage(token, conversationId)
         .enqueue(
            new Callback<MessageEntry>() {
               @Override
               public void onResponse(
                  Call<MessageEntry> call,
                  Response<MessageEntry> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                     MessageEntry messageEntry = response.body();
                     if (messageEntry != null) {
                        _decryptMessage(messageEntry);
                        messageDatabaseUtil.insertMessageEntry(messageEntry);
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch conversation")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<MessageEntry> call,
                  Throwable throwable
               ) {
                  callback.onFailure(call, throwable);
               }
            }
         );
   }

   @Override
   public void addMessage(
      MessageEntry messageEntry,
      Callback<MessageEntry> callback
   ) {
      messageDatabaseUtil.insertMessageEntry(messageEntry);

      messageApiService
         .addMessage(messageEntry, currentUser.getToken())
         .enqueue(
            new Callback<MessageEntry>() {
               @Override
               public void onResponse(
                  Call<MessageEntry> call,
                  Response<MessageEntry> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                     MessageEntry messageEntry = response.body();

                     messageEntry.setUploaded(true);
                     _decryptMessage(messageEntry);
                     messageDatabaseUtil.updateMessageEntry(messageEntry);
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch conversation")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<MessageEntry> call,
                  Throwable throwable
               ) {}
            }
         );
   }

   @Override
   public void addMessages(
      List<MessageEntry> messageEntries,
      Callback<List<MessageEntry>> callback
   ) {
      messageApiService
         .addMessages(messageEntries, currentUser.getToken())
         .enqueue(
            new Callback<List<MessageEntry>>() {
               @Override
               public void onResponse(
                  Call<List<MessageEntry>> call,
                  Response<List<MessageEntry>> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                     callback.onResponse(call, response);
                  }
               }

               @Override
               public void onFailure(
                  Call<List<MessageEntry>> call,
                  Throwable throwable
               ) {
                  callback.onFailure(call, throwable);
               }
            }
         );
   }

   @Override
   public void deleteMessage(
      String token,
      String messageUuid,
      Callback<Void> callback
   ) {
      messageApiService
         .deleteMessageByUuid(token, messageUuid)
         .enqueue(
            new Callback<Void>() {
               @Override
               public void onResponse(
                  Call<Void> call,
                  Response<Void> response
               ) {}

               @Override
               public void onFailure(Call<Void> call, Throwable throwable) {}
            }
         );
   }

   @Override
   public void getMessages(
      String token,
      Long conversationId,
      Callback<List<MessageEntry>> callback
   ) {
      List<MessageEntry> localMessages =
         messageDatabaseUtil.getAllMessageEntriesByConversationId(
            conversationId
         );
      if (!localMessages.isEmpty()) {
         if (
            System.currentTimeMillis() -
               localMessages.get(localMessages.size() - 1).getTimestamp() <
            AppConstants.MESSAGE_SYNC_TIME
         ) {
            callback.onResponse(null, Response.success(localMessages));
            return;
         }
      }

      messageApiService
         .getMessages(currentUser.getToken(), conversationId)
         .enqueue(
            new Callback<List<MessageEntry>>() {
               @Override
               public void onResponse(
                  Call<List<MessageEntry>> call,
                  Response<List<MessageEntry>> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                     for (MessageEntry messageEntry : response.body()) {
                        if (messageEntry != null) {
                           _decryptMessage(messageEntry);
                           messageDatabaseUtil.insertMessageEntry(messageEntry);
                        }
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch conversation")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<List<MessageEntry>> call,
                  Throwable throwable
               ) {
                  callback.onFailure(call, throwable);
               }
            }
         );
   }

   private void _decryptMessage(MessageEntry messageEntry) {
      messageEntry.setContent(messageEntry.getContentEncrypted());
      //messageEntry.setContent(EncryptionHelper.decrypt(messageEntry.getContentEncrypted(), currentUser.getPublicKey()));
   }
}
