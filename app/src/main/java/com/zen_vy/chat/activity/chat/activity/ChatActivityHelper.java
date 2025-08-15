package com.zen_vy.chat.activity.chat.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.user.entity.User;
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
