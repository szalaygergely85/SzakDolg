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
               .addHeader("Authorization", currentUser.getToken())
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

   public String getContent(MessageEntry messageEntry, User sender) {
      if (messageEntry.getType() == MessageTypeConstants.MESSAGE) {
         String decryptedContentString = null;
         messageEntry.setContent(messageEntry.getContentEncrypted());
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
         return "Received image from: " + sender.getDisplayName() ;
      }
      return null;
   }



   private boolean isSenderLoggedUser(MessageEntry messageEntry) {
      return currentUser.getUserId().equals(messageEntry.getSenderId());
   }

   public int getCountByNotReadMsg(Long conversationId) {
      return messageService.getCountByNotReadMsg(conversationId);
   }

}
