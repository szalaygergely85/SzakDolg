package com.example.szakdolg.activity.main.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.model.conversation.ConversationCoordinatorService;
import com.example.szakdolg.model.conversation.ConversationParticipantCoordinatorService;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.image.ImageCoordinatorService;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserCoordinatorService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAdapterHelper {

   private User currentUser;
   private Context context;
   private UserCoordinatorService userCoordinatorService;

   private ConversationCoordinatorService conversationCoordinatorService;

   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;

   private ImageCoordinatorService imageCoordinatorService;

   public MainAdapterHelper(User currentUser, Context context) {
      this.currentUser = currentUser;
      this.context = context;
      this.userCoordinatorService =
      new UserCoordinatorService(context, currentUser);
      this.conversationCoordinatorService =
      new ConversationCoordinatorService(context, currentUser);
      this.imageCoordinatorService =
      new ImageCoordinatorService(context, currentUser);
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
   }

   public void setImageView(List<User> participants, ImageView image) {
      if (participants.size() > 1) {
         Glide
            .with(context)
            .load(R.drawable.ic_group) // Load your drawable directly
            .into(image);
      } else {
         User user = userCoordinatorService.getUserByUserId(
            participants.get(0).getUserId(),
            currentUser
         );
         Uri uri = imageCoordinatorService.getImage(user);
         if (uri != null) {
            Glide
               .with(context)
               .load(uri)
               .placeholder(R.drawable.ic_blank_profile) // Default image while loading
               .error(R.drawable.ic_blank_profile) // Default image on error
               .into(image);
         }
      }
   }

   public List<User> getParticipantUser(Long conversationId) {
      List<ConversationParticipant> participants =
         conversationParticipantCoordinatorService.getOtherParticipants(
            conversationId
         );
      List<User> participantUsers = new ArrayList<>();
      if (participants != null) {
         for (ConversationParticipant participant : participants) {
            User user = userCoordinatorService.getUserByUserId(
               participant.getUserId(),
               currentUser
            );
            if (user != null) {
               participantUsers.add(user);
            } else {
               return null;
            }
         }

         return participantUsers;
      } else {
         return null;
      }
   }

   public String getConversationTitle(
      Long conversationId,
      List<User> participantUsers
   ) {
      Conversation conversation =
         conversationCoordinatorService.getConversation(conversationId);
      String conversationTitle = conversation.getConversationName();
      if (conversationTitle != null) {
         return conversationTitle;
      } else {
         if (participantUsers != null) {
            if (participantUsers.size() == 1) {
               return participantUsers.get(0).getDisplayName();
            } else {
               String title = null;
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

         try {
            /*
			if (isSenderLoggedUser(messageEntry)) {
				decryptedContentString =
				EncryptionHelper.decrypt(
					messageEntry.getContentSenderVersion(),
					KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
				);
			} else {
				decryptedContentString =
				EncryptionHelper.decrypt(
					messageEntry.getContent(),
					KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
				);
			}
*/

            decryptedContentString = messageEntry.getContentEncrypted();
            return decryptedContentString;
         } catch (Exception e) {
            throw new RuntimeException(e);
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
}
