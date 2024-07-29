package com.example.szakdolg.conversation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.chat.activity.ChatActivity;
import com.example.szakdolg.constans.IntentConstans;
import com.example.szakdolg.constans.MessageTypeConstans;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.CacheUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationApiHelper {

   private final String TAG = "ConversationApiHelper";

   private MessageApiHelper messageApiHelper = new MessageApiHelper();

   private ConversationApiService conversationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ConversationApiService.class);

   public void addNewConversationAndSendMessage(
      List<Long> userIds,
      String message,
      String token,
      User currentUser,
      Context context
   ) {
      Call<Long> call = conversationApiService.addNewConversation(
         userIds,
         token
      );

      call.enqueue(
         new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
               if (response.isSuccessful()) {
                  Long conversationId = response.body();
                  if (conversationId != null) {
                     messageApiHelper.sendMessageAndOpenChat(
                        conversationId,
                        new MessageEntry(
                           conversationId,
                           currentUser.getUserId(),
                           message,
                           MessageTypeConstans.MESSAGE,
                           message
                        ),
                        token
                     );
                     openConversation(
                        conversationId,
                        context,
                        currentUser,
                        token
                     );
                  }
               }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {}
         }
      );
   }

   public void openConversation(
      Context context,
      Long conversationId,
      List<User> participants,
      User loggedUser,
      String token
   ) {
      if (conversationId == null && participants != null) {
         Call<Long> call = conversationApiService.addConversation(
            participants,
            token
         );
         call.enqueue(
            new Callback<Long>() {
               @Override
               public void onResponse(
                  Call<Long> call,
                  Response<Long> response
               ) {
                  Log.e(TAG, "" + response.code());
                  if (response.isSuccessful()) {
                     openConversation(
                        response.body(),
                        context,
                        loggedUser,
                        token
                     );
                  } else {
                     Log.e(TAG, "" + response.code());
                     //TODO Handle the error
                  }
               }

               @Override
               public void onFailure(Call<Long> call, Throwable t) {
                  Log.e(TAG, "" + t.getMessage());
               }
            }
         );
      } else {
         Intent intent = new Intent(context, ChatActivity.class);
         intent.putExtra(
            SharedPreferencesConstans.CONVERSATION_ID,
            conversationId
         );
         intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
         context.startActivity(intent);
      }
   }

   public void openConversation(
      Long conversationId,
      Context context,
      User loggedUser,
      String token
   ) {
      Call<ConversationContent> call =
         conversationApiService.getConversationAndContentById(
            conversationId,
            token
         );
      call.enqueue(
         new Callback<ConversationContent>() {
            @Override
            public void onResponse(
               Call<ConversationContent> call,
               Response<ConversationContent> response
            ) {
               if (response.isSuccessful()) {
                  ConversationContent conversationContent = response.body();
                  List<User> allParticipants =
                     conversationContent.getParticipants();
                  User otherUser = UserUtil.removeCurrentUserFromList(
                     allParticipants,
                     loggedUser.getUserId()
                  );

                  if (otherUser.getPublicKey() != null) {
                     try {
                        CacheUtil.writePublicKeysCache(context, otherUser);
                     } catch (Exception e) {
                        throw new RuntimeException(e);
                     }
                  }

                  //TODO must be different with groupchats

                  Intent intent = new Intent(context, ChatActivity.class);
                  intent.putExtra(
                     IntentConstans.CONVERSATION_ID,
                     conversationId
                  );
                  intent.putExtra(IntentConstans.CURRENT_USER, loggedUser);
                  intent.putExtra(
                     IntentConstans.CONVERSATION_CONTENT,
                     conversationContent
                  );
                  context.startActivity(intent);
               }
            }

            @Override
            public void onFailure(
               Call<ConversationContent> call,
               Throwable t
            ) {}
         }
      );
   }
}
