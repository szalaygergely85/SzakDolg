package com.example.szakdolg.model.message.api;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.cache.CacheAction;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.message.MessageDatabaseUtil;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageApiHelper {

   private final String TAG = "MessageApiHelper";

   private Context context;
   private User currentUser;

   private MessageApiService messageApiService;

   private UserApiHelper userApiHelper = new UserApiHelper();

   public MessageApiHelper(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.messageApiService =
      RetrofitClient.getRetrofitInstance().create(MessageApiService.class);
      this.userApiHelper = userApiHelper;
   }

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
                     CacheAction.validateMessages(
                        response.body(),
                        context,
                        user
                     );
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

   public void getMessages(
      Long conversationId,
      Consumer<List<MessageEntry>> onSuccess
   ) {
      Call<List<MessageEntry>> call = messageApiService.getMessages(
         currentUser.getAuthToken(),
         conversationId
      );
      call.enqueue(
         new Callback<List<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<List<MessageEntry>> call,
               Response<List<MessageEntry>> response
            ) {
               if (response.isSuccessful()) {
                  List<MessageEntry> messages = response.body();
                  if (messages != null) {
                     onSuccess.accept(messages);
                  }
               } else {
                  // Handle the error
               }
            }

            @Override
            public void onFailure(Call<List<MessageEntry>> call, Throwable t) {
               // Handle the failure
            }
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
         messageDatabaseUtil.getAllMessageEntriesByConversationId(
            conversationId
         )
      );
   }

   public void sendMessageAndOpenChat(
      Long conversationId,
      MessageEntry messageEntry,
      String userToken
   ) {
      Call<MessageEntry> call = messageApiService.addMessage(
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
      Call<MessageEntry> call = messageApiService.addMessage(
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

   public void getNewMessages(
      Context context,
      String userToken,
      User user,
      Runnable runnable
   ) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );
      Call<List<MessageEntry>> messagesCall = messageApiService.getNewMessages(
         userToken
      );

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
                     setMessagesToDownloaded(messageIds);

                     runnable.run();
                  }
               }
            }

            @Override
            public void onFailure(Call<List<MessageEntry>> call, Throwable t) {}
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

   public void addMessage(
      MessageEntry messageEntry,
      Consumer<MessageEntry> onSuccess
   ) {
      Call<MessageEntry> call = messageApiService.addMessage(
         messageEntry,
         currentUser.getAuthToken()
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
                     if (message != null) {
                        onSuccess.accept(message);
                     }
                  }
               } else {
                  Log.e(AppConstants.LOG_TAG, "" + response.code());
               }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable t) {
               Log.e(AppConstants.LOG_TAG, "" + t.getMessage());
            }
         }
      );
   }
}
