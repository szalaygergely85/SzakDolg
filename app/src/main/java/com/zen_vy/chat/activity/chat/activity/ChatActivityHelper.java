package com.zen_vy.chat.activity.chat.activity;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.util.UserUtil;
import com.zen_vy.chat.util.UUIDUtil;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
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
      this.conversationService = new ConversationService(context, currentUser);

      this.currentUser = currentUser;

      this.messageService = new MessageService(context, currentUser);
   }

   public String getTime(Long time) {
      Date date = new Date(time);

      Format format = new SimpleDateFormat("HH:mm");
      return format.format(date);
   }

   public MessageEntry sendMessage(String content, int messageType) {
      MessageEntry messageEntry = new MessageEntry(
         null,
         conversation.getConversationId(),
         currentUser.getUserId(),
         System.currentTimeMillis(),
         null,
         false,
         messageType,
         content,
         UUIDUtil.UUIDGenerator(),
         false
      );

      MessageService messageService = new MessageService(context, currentUser);
      messageService.addMessage(
         messageEntry,
         new MessageService.MessageCallback<MessageEntry>() {
            @Override
            public void onSuccess(MessageEntry data) {
               Log.i(
                  AppConstants.LOG_TAG,
                  "Message sent to: " + messageEntry.getConversationId()
               );
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      return messageEntry;
   }

   public void setToolbarTitle(Toolbar mToolbar) {
      String conversationName = conversation.getConversationName();
      if (conversationName != null) {
         mToolbar.setTitle(conversation.getConversationName());
      } else {
         mToolbar.setTitle(_createTitleWithUsernames(users));
      }
   }

   private String _createTitleWithUsernames(List<User> users) {
      String title = "";
      for (User user : UserUtil.removeCurrentUserFromList(
         users,
         currentUser.getUserId()
      )) {
         if (users.indexOf(user) == 0) {
            title = user.getDisplayName();
         } else {
            title += " " + user.getDisplayName();
         }
      }
      return title;
   }

   public void setMessagesRead() {
      messageService.setMessagesAsReadByConversationId(
         conversation.getConversationId()
      );
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
