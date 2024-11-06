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
import com.example.szakdolg.model.user.service.UserService;
import java.util.ArrayList;
import java.util.List;

public class MainAdapterHelper {

   private User currentUser;
   private Context context;
   private UserService userService;

   private ConversationCoordinatorService conversationCoordinatorService;

   private ConversationParticipantCoordinatorService conversationParticipantCoordinatorService;

   private ImageCoordinatorService imageCoordinatorService;

   public MainAdapterHelper(User currentUser, Context context) {
      this.currentUser = currentUser;
      this.context = context;
      this.userService = new UserService(context);
      this.conversationCoordinatorService = new ConversationCoordinatorService(context, currentUser);
      this.imageCoordinatorService = new ImageCoordinatorService(context, currentUser);
      this.conversationParticipantCoordinatorService =
      new ConversationParticipantCoordinatorService(context, currentUser);
   }

   public void setImageView(Long userId, ImageView image) {
      User user = userService.getUserByUserId(userId, currentUser);

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

   public List<User> getParticipantUser(Long conversationId) {
      //TODO itt kezdtem el....
      List<ConversationParticipant> participants =
              conversationParticipantCoordinatorService.getOtherParticipants(conversationId);
      List<User> participantUsers = new ArrayList<>();
      for (ConversationParticipant participant : participants) {
         User user = userService.getUserByUserId(
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
   }

   public String getConversationTitle(
      Long conversationId,
      List<User> participantUsers
   ) {
      Conversation conversation = conversationCoordinatorService.getConversation(
         conversationId
      );
      String conversationTitle = conversation.getConversationName();
      if (conversationTitle != null) {
         return conversationTitle;
      } else {
         if (participantUsers.size() == 1) {
            return participantUsers.get(0).getDisplayName();
         } else {
            String title = null;
            for (User user : participantUsers) {
               title += user.getDisplayName() + ", ";
            }
            return title;
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

            decryptedContentString = messageEntry.getContent();
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
}
