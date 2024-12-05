package com.example.szakdolg.activity.chat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.conversation.ConversationParticipantCoordinatorService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.message.MessageCoordinatorService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserCoordinatorService;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.UUIDUtil;
import java.util.ArrayList;
import java.util.List;

public class ChatActivityHelper {

   private Conversation conversation;
   private User currentUser;
   private AppCompatActivity context;

   private ConversationCoordinatorService conversationCoordinatorService;
   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;

   private UserCoordinatorService userCoordinatorService;

   private MessageCoordinatorService messageCoordinatorService;

   public ChatActivityHelper(
      AppCompatActivity context,
      Long conversationId,
      User currentUser
   ) {
      this.context = context;
      this.conversationCoordinatorService =
      new ConversationCoordinatorService(context, currentUser);
      this.conversation =
      conversationCoordinatorService.getConversation(conversationId);
      this.currentUser = currentUser;
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
      this.userCoordinatorService = new UserCoordinatorService(context);
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
      chatRecView.scrollToPosition(adapter.getItemCount() - 1);
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
   }

   public void reloadMessages(ChatAdapter adapter) {
      adapter.setMessageEntries(
         messageCoordinatorService.getMessagesByConversationId(
            conversation.getConversationId()
         )
      );
   }

   public void setToolbarTitle(Toolbar mToolbar, Long conversationId) {
      String conversationName = conversation.getConversationName();
      if (conversationName!=null){
         mToolbar.setTitle(conversation.getConversationName());
      }
      else{

         mToolbar.setTitle(_createTitleWithUsernames(getUsers()));
      }

   }

   private String _createTitleWithUsernames(List<User> users){
      String title = null;
      for (User user: users){
         if(users.indexOf(user)==0){
            title =user.getDisplayName();
         }else {
            title +=" "+user.getDisplayName();
         }
      }
      return title;
   }

   /*
public void _startRepeatingTask() {
	runnable =
	new Runnable() {
		@Override
		public void run() {
			try {
			messageApiHelper.getNewMessages(
				ChatActivity.this,
				token,
				currentUser,
				() -> {
					messageApiHelper.reloadMessages(
						ChatActivity.this,
						conversationId,
						adapter,
						currentUser
					);
				}
			);
			} finally {
			handler.postDelayed(runnable, 15000);
			}
		}
	};

	runnable.run();
}
*/
}
