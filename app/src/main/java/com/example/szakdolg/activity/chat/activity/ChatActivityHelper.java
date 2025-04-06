package com.example.szakdolg.activity.chat.activity;

import android.icu.text.DateFormat;
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
import com.example.szakdolg.models.user.util.UserUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

   public void setMessageBoard(RecyclerView chatRecView, ChatAdapter adapter) {
      messageService.getMessagesByConversationId(
         conversation.getConversationId(),
         new MessageService.MessageCallback<List<MessageEntry>>() {
            @Override
            public void onSuccess(List<MessageEntry> messageEntries) {
               messageEntries.sort(
                  Comparator.comparingLong(MessageEntry::getTimestamp)
               );

               adapter.setMessageEntries(_prepareMessageList(messageEntries));
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      chatRecView.setAdapter(adapter);
      chatRecView.setLayoutManager(new LinearLayoutManager(context));
      chatRecView.scrollToPosition(adapter.getItemCount() - 1);
   }

   private List<Object> _prepareMessageList(List<MessageEntry> messageEntries) {
      List<Object> sortedList = new ArrayList<>();
      long previousTimestamp = 0L;
      for (MessageEntry messageEntry : messageEntries) {
         if (isNewDay(previousTimestamp, messageEntry.getTimestamp())) {
            DateFormat dateFormat = DateFormat.getDateInstance(
               DateFormat.SHORT,
               Locale.getDefault()
            );

            sortedList.add(
               dateFormat.format(new Date(messageEntry.getTimestamp()))
            );
         }

         sortedList.add(messageEntry);
         previousTimestamp = messageEntry.getTimestamp();
      }
      return sortedList;
   }

   private boolean isNewDay(long previousTimestamp, long currentTimestamp) {
      Date currentDate = new Date(currentTimestamp);
      Date previousDate = new Date(previousTimestamp);

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      String currentDateStr = dateFormat.format(currentDate);
      String previousDateStr = dateFormat.format(previousDate);

      return !currentDateStr.equals(previousDateStr);
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
