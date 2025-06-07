package com.zen_vy.chat.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.zen_vy.chat.R;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.util.UserUtil;
import com.zen_vy.chat.util.EncryptionHelper;
import com.zen_vy.chat.util.KeyStoreUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MainAdapterHelper {

   private final User currentUser;
   private final Context context;

   private final MessageService messageService;

   public MainAdapterHelper(User currentUser, Context context) {
      this.currentUser = currentUser;
      this.context = context;
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

         GlideUrl glideUrl = new GlideUrl(
                 imageUrl,
                 new LazyHeaders.Builder()
                         .addHeader("Authorization",  currentUser.getToken())
                         .build()
         );


            Glide
                    .with(context)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_blank_profile)
                    .error(R.drawable.ic_blank_profile)
                    .into(image);

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
               return "You: " + messageEntry.getContent();
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
               Timber.e(e);
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
