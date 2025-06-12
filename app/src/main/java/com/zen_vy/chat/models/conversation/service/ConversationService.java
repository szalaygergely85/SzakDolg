package com.zen_vy.chat.models.conversation.service;

import android.content.Context;
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.models.conversation.db.ConversationDatabaseUtil;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.repository.ConversationRepository;
import com.zen_vy.chat.models.conversation.repository.ConversationRepositoryImpl;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationService {

   private ConversationDatabaseUtil conversationDatabaseUtil;
   ConversationRepository conversationRepository;
   private Context context;
   private User currentUser;

   public ConversationService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.conversationRepository =
      new ConversationRepositoryImpl(context, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(context, currentUser);
   }

   public void getConversation(
      Long conversationId,
      final ConversationService.ConversationCallback<ConversationDTO> callback
   ) {
      conversationRepository.getConversation(
         conversationId,
         currentUser.getToken(),
         new Callback<ConversationDTO>() {
            @Override
            public void onResponse(
               Call<ConversationDTO> call,
               Response<ConversationDTO> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(
                     new Throwable("Failed to update Conversation")
                  );
               }
            }

            @Override
            public void onFailure(
               Call<ConversationDTO> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void getAllConversations(
      final ConversationService.ConversationCallback<
         List<ConversationDTO>
      > callback
   ) {
      conversationRepository.getAllConversation(
         currentUser.getToken(),
         new Callback<List<ConversationDTO>>() {
            @Override
            public void onResponse(
               Call<List<ConversationDTO>> call,
               Response<List<ConversationDTO>> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(
               Call<List<ConversationDTO>> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void addConversation(
      Conversation conversation,
      final ConversationService.ConversationCallback<Conversation> callback
   ) {
      conversationRepository.addConversation(
         conversation,
         currentUser.getToken(),
         new Callback<Conversation>() {
            @Override
            public void onResponse(
               Call<Conversation> call,
               Response<Conversation> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(
               Call<Conversation> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public void addConversationByUserId(
      List<Long> userIds,
      final ConversationService.ConversationCallback<ConversationDTO> callback
   ) {
      conversationRepository.addConversationByUserId(
         userIds,
         currentUser.getToken(),
         new Callback<ConversationDTO>() {
            @Override
            public void onResponse(
               Call<ConversationDTO> call,
               Response<ConversationDTO> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(
               Call<ConversationDTO> call,
               Throwable throwable
            ) {
               callback.onError(throwable);
            }
         }
      );
   }

   public interface ConversationCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
