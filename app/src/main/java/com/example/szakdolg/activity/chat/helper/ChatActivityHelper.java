package com.example.szakdolg.activity.chat.helper;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.conversation.ConversationParticipantCoordinatorService;
import com.example.szakdolg.model.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.message.MessageCoordinatorService;
import com.example.szakdolg.model.message.MessageDatabaseUtil;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserCoordinatorService;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.UUIDUtil;
import java.util.ArrayList;
import java.util.List;

public class ChatActivityHelper {

   private Conversation conversation;
   private User currentUser;

   private String authToken;
   private ChatAdapter chatAdapter;
   private ConversationDatabaseUtil conversationDatabaseUtil;

   private UserDatabaseUtil userDatabaseUtil;

   private MessageDatabaseUtil messageDatabaseUtil;

   private Context context;

   private MessageApiHelper messageApiHelper;
   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;

   private UserCoordinatorService userCoordinatorService;

   private MessageCoordinatorService messageCoordinatorService;

   public ChatActivityHelper(
      Context context,
      Conversation conversation,
      User currentUser,
      String authToken,
      ChatAdapter chatAdapter
   ) {
      this.context = context;
      this.conversation = conversation;
      this.currentUser = currentUser;
      this.authToken = authToken;
      this.chatAdapter = chatAdapter;
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
      this.userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(context, currentUser);
      this.messageApiHelper = new MessageApiHelper(context, currentUser);
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
      this.userCoordinatorService =
      new UserCoordinatorService(context, currentUser);
      this.messageCoordinatorService =
      new MessageCoordinatorService(context, currentUser);
   }

   private List<User> getUsers() {
      List<ConversationParticipant> participants =
         conversationParticipantCoordinatorService.getOtherParticipants(
            conversation.getConversationId()
         );
      List<User> users = new ArrayList<>();
      for (ConversationParticipant participant : participants) {
         User user = userCoordinatorService.getUserByUserId(
            participant.getUserId(),
            currentUser
         );
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   public void setMessageBoard(RecyclerView chatRecView, ChatAdapter adapter) {
      adapter.setUsers(getUsers());
      adapter.setMessageEntries(
         messageCoordinatorService.getMessagesByConversationId(
            conversation.getConversationId()
         )
      );

      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(context));
   }

   public void sendMessage(String content, int messageType) {
      List<User> users = getUsers();
      if (conversation.getNumberOfParticipants() > 2) {
         //TODO GROUP Conversation
      } else {
         User user = users.get(0);

         //TODO better key system needed.....
         String encryptedContentString = EncryptionHelper.encrypt(
            content,
            CacheUtil.getPublicKeyFromCache(context, user.getEmail())
         );

         MessageEntry messageEntry = new MessageEntry(
            null,
            conversation.getConversationId(),
            currentUser.getUserId(),
            System.currentTimeMillis(),
            encryptedContentString,
            false,
            messageType,
            content,
            UUIDUtil.UUIDGenerator()
         );

         messageCoordinatorService.addMessage(messageEntry);
      }
      /*
	messageApiHelper.sendMessage(
		context,
		conversationId,
		messageEntry,
		chatAdapter,
		authToken,
		currentUser
	);*/
   }
}
