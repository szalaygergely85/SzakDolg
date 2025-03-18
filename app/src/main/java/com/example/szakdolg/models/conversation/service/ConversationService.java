package com.example.szakdolg.models.conversation.service;

import android.content.Context;

import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.repository.ConversationRepository;
import com.example.szakdolg.models.conversation.repository.ConversationRepositoryImpl;
import com.example.szakdolg.models.user.entity.User;
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
      this.conversationRepository = new ConversationRepositoryImpl(context, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(context, currentUser);
   }

   public Conversation addConversation(Conversation conversation) {
      List<Conversation> localConversations =
         conversationDatabaseUtil.getAllConversations();
      if (!localConversations.contains(conversation)) {
         conversationDatabaseUtil.insertConversation(conversation);
         return conversation;
      } else {
         return null;
      }
   }

   public List<Conversation> getAllConversations() {
      return conversationDatabaseUtil.getAllConversations();
   }

   public Conversation getConversation(Long conversationId) {
      return conversationDatabaseUtil.getConversationById(conversationId);
   }

   public void addConversations(List<Conversation> conversationsRemote) {
      for (Conversation conversation : conversationsRemote) {
         addConversation(conversation);
      }
   }

   //Starting the new Repository way from here

   public void addConversation(Conversation conversation, final ConversationService.ConversationCallback<Conversation> callback){
      conversationRepository.addConversation(conversation, currentUser.getAuthToken(), new Callback<Conversation>() {
         @Override
         public void onResponse(Call<Conversation> call, Response<Conversation> response) {
            if (response.isSuccessful()) {
               callback.onSuccess(null);
            } else {
               callback.onError(new Throwable("Failed to update contact"));
            }
         }

         @Override
         public void onFailure(Call<Conversation> call, Throwable throwable) {
            callback.onError(throwable);
         }
      });
   }

   public void addConversationByUserId(List<Long> userIds, final ConversationService.ConversationCallback<Conversation> callback){
      conversationRepository.addConversationByUserId(userIds, currentUser.getAuthToken(), new Callback<Conversation>() {
         @Override
         public void onResponse(Call<Conversation> call, Response<Conversation> response) {
            if (response.isSuccessful()) {
               callback.onSuccess(response.body());
            } else {
               callback.onError(new Throwable("Failed to update contact"));
            }
         }

         @Override
         public void onFailure(Call<Conversation> call, Throwable throwable) {
            callback.onError(throwable);
         }
      });
   }


   public interface ConversationCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
