package com.example.szakdolg.activity.chat.activity;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.message.MessageService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.UUIDUtil;

import java.util.List;

public class ChatActivityHelper {

   private Conversation conversation;
   private User currentUser;

   private List<User> users;
   private AppCompatActivity context;

   private ConversationService conversationService;
   private MessageService messageService;

   public ChatActivityHelper(
      AppCompatActivity context,
      Conversation conversation,
      User currentUser,
      List<User> users
   ) {
      this.conversation = conversation;
      this.context = context;
      this.users = users;
      this.conversationService =
      new ConversationService(context, currentUser);

      this.currentUser = currentUser;


      this.messageService = new MessageService(context, currentUser);
   }

   public void setMessageBoard(RecyclerView chatRecView, ChatAdapter adapter) {
      adapter.setUsers(users);
      messageService.getMessagesByConversationId(conversation.getConversationId(), new MessageService.MessageCallback<List<MessageEntry>>() {
         @Override
         public void onSuccess(List<MessageEntry> messageEntries) {
            adapter.setMessageEntries(
                    messageEntries
            );
         }

         @Override
         public void onError(Throwable t) {

         }
      });



      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(context));
      chatRecView.scrollToPosition(adapter.getItemCount() - 1);
   }

   public MessageEntry sendMessage(String content, int messageType) {
      if (conversation.getNumberOfParticipants() > 2) {
         //TODO GROUP Conversation
         return null;
      } else {
         User user = users.get(0);

         String publicKey = user.getPublicKey();

         String encryptedContentString = null;
         if (publicKey != null) {
            encryptedContentString =
            EncryptionHelper.encrypt(content, user.getPublicKey());
         }

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

         MessageService messageService = new MessageService(context, currentUser);
         messageService.addMessage(messageEntry, new MessageService.MessageCallback<MessageEntry>() {
            @Override
            public void onSuccess(MessageEntry data) {
               Log.i(AppConstants.LOG_TAG, "Message sent to: " + messageEntry.getConversationId());
            }

            @Override
            public void onError(Throwable t) {

            }
         });

         return messageEntry;
      }

   }

   public void setToolbarTitle(Toolbar mToolbar, Long conversationId) {
      String conversationName = conversation.getConversationName();
      if (conversationName != null) {
         mToolbar.setTitle(conversation.getConversationName());
      } else {
         mToolbar.setTitle(_createTitleWithUsernames(users));
      }
   }

   private String _createTitleWithUsernames(List<User> users) {
      String title = null;
      for (User user : users) {
         if (users.indexOf(user) == 0) {
            title = user.getDisplayName();
         } else {
            title += " " + user.getDisplayName();
         }
      }
      return title;
   }

   public void setMessagesRead() {
      messageService.setMessagesAsReadByConversationId(conversation.getConversationId());
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
