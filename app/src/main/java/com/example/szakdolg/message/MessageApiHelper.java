package com.example.szakdolg.message;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.chat.adapter.ChatAdapter;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.file.apiservice.FileApiService;
import com.example.szakdolg.messageboard.DTO.MessageBoard;
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
   private FileApiService fileApiService = RetrofitClient
      .getRetrofitInstance()
      .create(FileApiService.class);

   private UserApiHelper userApiHelper = new UserApiHelper();
   User loggedUser;

   public void checkCachedMessages(String authToken, Context context) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(context);
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
                     CacheUtil.validateMessages(response.body(), context);
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
      Long conversationId,
      ChatAdapter adapter,
      String userToken
   ) {
      Call<ArrayList<MessageEntry>> call =
         messageApiService.getConversationMessages(conversationId, userToken);

      call.enqueue(
         new Callback<ArrayList<MessageEntry>>() {
            @Override
            public void onResponse(
               Call<ArrayList<MessageEntry>> call,
               Response<ArrayList<MessageEntry>> response
            ) {
               Log.e(TAG, "" + response.code());

               if (response.isSuccessful()) {
                  ArrayList<MessageEntry> messageEntryList = response.body();
                  if (messageEntryList != null) {
                     adapter.setMessageEntries(messageEntryList);
                  }
               } else {
                  Log.e(TAG, "" + response.code());
                  //TODO Handle the error
               }
            }

            @Override
            public void onFailure(
               Call<ArrayList<MessageEntry>> call,
               Throwable t
            ) {
               Log.e(TAG, "" + t.getMessage());
            }
         }
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
      Long conversationId,
      MessageEntry messageEntry,
      ChatAdapter adapter,
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

                  reloadMessages(conversationId, adapter, userToken);
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

   public void getLatestMessages(
      MessageBoardAdapter adapter,
      Context context,
      String userToken,
      User loggedUser
   ) {
      //  this.loggedUser=loggedUser;

      Call<ArrayList<MessageBoard>> messagesCall =
         messageApiService.getLatestMessages(userToken);

      messagesCall.enqueue(
         new Callback<ArrayList<MessageBoard>>() {
            @Override
            public void onResponse(
               Call<ArrayList<MessageBoard>> call,
               Response<ArrayList<MessageBoard>> response
            ) {
               if (response.isSuccessful()) {
                  Log.e(TAG, "" + response.code());

                  //TODO need to use uuid for users.... or userid

                  ArrayList<MessageBoard> messageBoards = response.body();
                  for (MessageBoard messageBoard : messageBoards) {
                     User user = _findOtherUserById(
                        messageBoard.getParticipants(),
                        loggedUser.getUserId()
                     );
                     if (user.getPublicKey() != null) {
                        try {
                           CacheUtil.writePublicKeysCache(context, user);
                        } catch (Exception e) {
                           throw new RuntimeException(e);
                        }
                     }
                  }
                  adapter.setMessageB(messageBoards);
               }
            }

            @Override
            public void onFailure(
               Call<ArrayList<MessageBoard>> call,
               Throwable t
            ) {
               Log.e(TAG, "" + t.getMessage());
            }
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
