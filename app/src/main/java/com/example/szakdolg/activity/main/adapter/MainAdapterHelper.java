package com.example.szakdolg.activity.main.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.image.ImageCoordinatorService;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.message.MessageService;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.util.UserUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAdapterHelper {

   private final User currentUser;
   private final Context context;

   private final ConversationService conversationService;

   private final ImageCoordinatorService imageCoordinatorService;
   private final MessageService messageService;

   public MainAdapterHelper(User currentUser, Context context) {
      this.currentUser = currentUser;
      this.context = context;
      this.conversationService = new ConversationService(context, currentUser);
      this.imageCoordinatorService =
      new ImageCoordinatorService(context, currentUser);
      this.messageService = new MessageService(context, currentUser);
   }

   public void setImageView(List<User> participants, ImageView image) {
      if (participants.size() > 1) {
         Glide
            .with(context)
            .load(R.drawable.ic_group) // Load your drawable directly
            .into(image);
      } else {
         UserUtil.removeCurrentUser(participants, currentUser.getUserId());

         String imageUrl = ImageUtil.buildProfileImageUrl(
            participants.get(0).getUserId()
         );
         if (imageUrl != null) {
            Glide
               .with(context)
               .load(imageUrl)
               .placeholder(R.drawable.ic_blank_profile)
               .error(R.drawable.ic_blank_profile)
               .into(image);
         } else {
            image.setImageResource(R.drawable.ic_blank_profile);
         }
      }
   }

   public String getConversationTitle(
      Conversation conversation,
      List<User> participantUsers
   ) {
      String conversationTitle = conversation.getConversationName();
      if (conversationTitle != null) {
         return conversationTitle;
      } else {
         if (participantUsers != null) {
            if (participantUsers.size() == 1) {
               return participantUsers.get(0).getDisplayName();
            } else {
               String title = "";
               for (User user : participantUsers) {
                  title += user.getDisplayName() + ", ";
               }
               return title;
            }
         } else {
            return null;
         }
      }
   }

   public String getContent(MessageEntry messageEntry) {
      if (messageEntry.getType() == MessageTypeConstants.MESSAGE) {
         String decryptedContentString = null;

         if (messageEntry.getContent() != null) {
            if (isSenderLoggedUser(messageEntry)) {
               return "You " + messageEntry.getContent();
            } else {
               return messageEntry.getContent();
            }
         } else {
            try {
               decryptedContentString =
               EncryptionHelper.decrypt(
                  messageEntry.getContent(),
                  KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
               );
               if (decryptedContentString != null) {
                  return decryptedContentString;
               }
            } catch (Exception e) {
               Log.e(AppConstants.LOG_TAG, e.toString());
            }
         }
      }
      if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
         return "Image received";
      }
      return null;
   }

   public String getTime(Long timestamp) {
      // Convert timestamp to Date
      Date date = new Date(timestamp);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);

      // Get current time for comparison
      Calendar now = Calendar.getInstance();

      // Formatter for today (hours and minutes)
      SimpleDateFormat todayFormat = new SimpleDateFormat(
         "HH:mm",
         Locale.getDefault()
      );

      // Formatter for day of the week (e.g., "Monday")
      SimpleDateFormat dayFormat = new SimpleDateFormat(
         "EEEE",
         Locale.getDefault()
      );

      // Formatter for full date (e.g., "MMM dd, yyyy")
      SimpleDateFormat dateFormat = new SimpleDateFormat(
         "MMM dd, yyyy",
         Locale.getDefault()
      );

      // Check if it's today
      if (
         now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
         now.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
      ) {
         // Return hours and minutes if it's today
         return todayFormat.format(date);
      }
      // Check if it's within the last week
      else if (
         now.getTimeInMillis() - calendar.getTimeInMillis() <
         7 * 24 * 60 * 60 * 1000
      ) {
         // Return day of the week if it's within the last 7 days
         return dayFormat.format(date);
      } else {
         // Otherwise, return full date
         return dateFormat.format(date);
      }
   }

   private boolean isSenderLoggedUser(MessageEntry messageEntry) {
      return currentUser.getUserId().equals(messageEntry.getSenderId());
   }

   public int getCountByNotReadMsg(Long conversationId) {
      return messageService.getCountByNotReadMsg(conversationId);
   }
}
