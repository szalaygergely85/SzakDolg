package com.example.szakdolg.models.conversation.service;

import android.content.Context;
import com.example.szakdolg.models.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;

public class ConversationService {

   private ConversationDatabaseUtil conversationDatabaseUtil;
   private Context context;
   private User currentUser;

   public ConversationService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
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
}
