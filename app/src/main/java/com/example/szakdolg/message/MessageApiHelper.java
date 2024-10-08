package com.example.szakdolg.message;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;

import com.example.szakdolg.chat.adapter.ChatAdapter;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.file.apiservice.FileApiService;
import com.example.szakdolg.messageboard.adapter.MessageBoardAdapter;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.api.UserApiHelper;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.CacheUtil;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageApiHelper {

   private final String TAG = "MessageApiHelper";

   private MessageApiService messageApiService = RetrofitClient
      .getRetrofitInstance()
      .create(MessageApiService.class);

   private UserApiHelper userApiHelper = new UserApiHelper();
   User loggedUser;

   public void checkCachedMessages(
      String authToken,
      Context context,
      User user
   ) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );
      Call<ArrayList<MessageEntry>> call =
         messageApiService.getMessagesAndCompareWithLocal(
            messageDatabaseUtil.getMessageEntryCount(),
            authToken
         );

      call.enqueue(
         new Callback<ArrayList<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<ArrayList<MessageEntry>> call,
               Response<ArrayList<MessageEntry>> response
            ) {
               if (response.isSuccessful()) {
                  if (response.body().size() > 0) {
                     CacheUtil.validateMessages(response.body(), context, user);
                  }
               }
            }

            @Override
            public void onFailure(
               Call<ArrayList<MessageEntry>> call,
               Throwable t
            ) {}
         }
      );
   }

   public void reloadMessages(
      Context context,
      Long conversationId,
      ChatAdapter adapter,
      User user
   ) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );
      adapter.setMessageEntries(
         messageDatabaseUtil.getAllMessageEntriesOfConversation(conversationId)
      );
   }

   public void sendMessageAndOpenChat(
      Long conversationId,
      MessageEntry messageEntry,
      String userToken
   ) {
      Call<MessageEntry> call = messageApiService.sendMessage(
         messageEntry,
         userToken
      );

      call.enqueue(
         new Callback<MessageEntry>() {
            @Override
            public void onResponse(
               Call<MessageEntry> call,
               Response<MessageEntry> response
            ) {
               Log.e(TAG, "" + response.code());

               if (response.isSuccessful()) {
                  Log.e(TAG, "" + response.body());
               } else {
                  Log.e(TAG, "" + response.code());
                  //TODO Handle the error
               }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable t) {
               Log.e(TAG, "" + t.getMessage());
            }
         }
      );
   }

   public void sendMessage(
      Context context,
      Long conversationId,
      MessageEntry messageEntry,
      ChatAdapter adapter,
      String userToken,
      User user
   ) {
      Call<MessageEntry> call = messageApiService.sendMessage(
         messageEntry,
         userToken
      );
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );

      call.enqueue(
         new Callback<MessageEntry>() {
            @Override
            public void onResponse(
               Call<MessageEntry> call,
               Response<MessageEntry> response
            ) {
               Log.e(TAG, "" + response.code());

               if (response.isSuccessful()) {
                  if (response.body() != null) {
                     MessageEntry message = response.body();
                     messageDatabaseUtil.insertMessageEntry(message);

                     reloadMessages(context, conversationId, adapter, user);
                  }
               } else {
                  Log.e(TAG, "" + response.code());
                  //TODO Handle the error
               }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable t) {
               Log.e(TAG, "" + t.getMessage());
            }
         }
      );
   }

   public void getNewMessages(Context context, String userToken, User user, MessageBoardAdapter messageBoardAdapter) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );
      Call<List<MessageEntry>> messagesCall =
         messageApiService.getNewMessages(userToken);

      messagesCall.enqueue(
         new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<List<MessageEntry>> call,
               Response<List<MessageEntry>> response
            ) {
               if (response.isSuccessful()) {
                  Log.e(TAG, "" + response.code());



                  List<MessageEntry> messages = response.body();
                  List<Long> messageIds = new ArrayList<>();
                  if (messages.size() > 0) {
                     for (MessageEntry messageEntry : messages) {
                        messageDatabaseUtil.insertMessageEntry(messageEntry);
                        messageIds.add(messageEntry.getMessageId());
                     }

                      messageBoardAdapter.notifyDataSetChanged();
                     setMessagesToDownloaded(messageIds);
                  }
               }
            }

            @Override
            public void onFailure(
               Call<List<MessageEntry>> call,
               Throwable t
            ) {}
         }
      );
   }

   public void setMessagesToDownloaded(List<Long> messageIds) {
      Call<String> messagesCall = messageApiService.markMessagesAsDownloaded(
         messageIds
      );
      messagesCall.enqueue(
         new Callback<String>() {
            @Override
            public void onResponse(
               Call<String> call,
               Response<String> response
            ) {
               Log.e(TAG, "" + response.code());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
         }
      );
   }

   private User _findOtherUserById(List<User> users, Long id) {
      for (User user : users) {
         if (!user.getUserId().equals(id)) {
            return user;
         }
      }
      return null; // or throw an exception, or return an Optional<User>
   }
}
