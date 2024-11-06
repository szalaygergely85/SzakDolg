package com.example.szakdolg.model.conversation.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.szakdolg.DTO.ConversationContent;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.cache.CacheAction;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.util.UserUtil;
import com.example.szakdolg.util.CacheUtil;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationApiHelper extends BaseService {

   private final String TAG = "ConversationApiHelper";

   private MessageApiHelper messageApiHelper;

   private ConversationApiService conversationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ConversationApiService.class);

    public ConversationApiHelper(Context context, User currentUser) {
        super(context, currentUser);
        this.messageApiHelper = new MessageApiHelper(context, currentUser);;
    }

    public void getConversation(Long conversationId, Consumer<Conversation> onSuccess) {

        Call<Conversation> call = conversationApiService.getConversation(conversationId, currentUser.getAuthToken());
        call.enqueue(
                new Callback<Conversation>() {
                    @Override
                    public void onResponse(
                            Call<Conversation> call,
                            Response<Conversation> response
                    ) {
                        if (response.isSuccessful()) {
                            if (response.body()!=null) {
                                Conversation conversation= response.body();
                                if (conversation!=null){
                                    onSuccess.accept(conversation);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Conversation> call,
                            Throwable t
                    ) {
                        Log.e(AppConstants.LOG_TAG, t.getMessage());
                    }
                }
        );
    }

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
                           MessageTypeConstants.MESSAGE,
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
         intent.putExtra(IntentConstants.CONVERSATION_ID, conversationId);
         intent.putExtra(SharedPreferencesConstants.CURRENT_USER, loggedUser);
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
                  List<User> otherUsers = UserUtil.removeCurrentUserFromList(
                     allParticipants,
                     loggedUser.getUserId()
                  );
                  if (otherUsers.size() == 1) {
                     User otherUser = otherUsers.get(0);
                     if (otherUser.getPublicKey() != null) {
                        try {
                           CacheUtil.writePublicKeysCache(context, otherUser);
                        } catch (Exception e) {
                           throw new RuntimeException(e);
                        }
                     }
                  }

                  //TODO must be different with groupchats

                  Intent intent = new Intent(context, ChatActivity.class);
                  intent.putExtra(
                     IntentConstants.CONVERSATION_ID,
                     conversationId
                  );
                  intent.putExtra(IntentConstants.CURRENT_USER, loggedUser);
                  intent.putExtra(
                     IntentConstants.CONVERSATION_CONTENT,
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

   public void checkCachedConversation(
      String token,
      Context context,
      User user
   ) {
      ConversationDatabaseUtil conversationDatabaseUtil =
         new ConversationDatabaseUtil(context, user);
      Call<List<Conversation>> call =
         conversationApiService.getConversationAndCompareWithLocal(
            conversationDatabaseUtil.getConversationCount(),
            token
         );

      call.enqueue(
         new Callback<List<Conversation>>() {
            @Override
            public void onResponse(
               Call<List<Conversation>> call,
               Response<List<Conversation>> response
            ) {
               if (response.isSuccessful()) {
                  if (response.body().size() > 0) {
                     CacheAction.validateConversation(
                        response.body(),
                        context,
                        user
                     );
                  }
               }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {}
         }
      );
   }

   public void checkCachedConversationParticipant(
      String token,
      Context context,
      User user
   ) {
      ConversationDatabaseUtil conversationDatabaseUtil =
         new ConversationDatabaseUtil(context, user);
      Call<List<ConversationParticipant>> call =
         conversationApiService.getConversationParticipantAndCompareWithLocal(
            conversationDatabaseUtil.getConversationParticipantCount(),
            token
         );
      call.enqueue(
         new Callback<List<ConversationParticipant>>() {
            @Override
            public void onResponse(
               Call<List<ConversationParticipant>> call,
               Response<List<ConversationParticipant>> response
            ) {
               if (response.isSuccessful()) {
                  if (response.body().size() > 0) {
                     CacheAction.validateConversationParticipant(
                        response.body(),
                        context,
                        user
                     );
                  }
               }
            }

            @Override
            public void onFailure(
               Call<List<ConversationParticipant>> call,
               Throwable t
            ) {}
         }
      );
   }
}
