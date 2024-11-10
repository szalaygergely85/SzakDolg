package com.example.szakdolg.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.viewholder.ImageViewHolder;
import com.example.szakdolg.activity.chat.viewholder.TextViewHolder;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final String TAG = "ChatAdapter";
   private final Context mContext;
   private List<MessageEntry> messageEntries = new ArrayList<>();

   private MessageApiHelper messageApiHelper;
   private final User currentUser;

   long time;

   public ChatAdapter(Context mContext, User user) {
      this.mContext = mContext;
      this.currentUser = user;
      this.messageApiHelper = new MessageApiHelper(mContext, user);
   }

   @Override
   public int getItemViewType(int position) {
      return messageEntries.get(position).getType();
   }

   @NonNull
   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      if (viewType == MessageTypeConstants.MESSAGE) {
         View view = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.chat_item, parent, false);
         return new TextViewHolder(view);
      } else if (viewType == MessageTypeConstants.IMAGE) {
         View view = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.chat_image_item, parent, false);
         return new ImageViewHolder(view);
      }
      return null;
   }

   @Override
   public void onBindViewHolder(
      @NonNull RecyclerView.ViewHolder holder,
      int position
   ) {
      MessageEntry messageEntry = messageEntries.get(
         holder.getAdapterPosition()
      );

      time = (messageEntry.getTimestamp());

         Date date = new Date(time);

         Format format = new SimpleDateFormat("HH:mm");
         String timeForm = format.format(date);
         if (messageEntry.getType() == MessageTypeConstants.MESSAGE) {
            String decryptedContentString = null;/*
		try {
			if (isSenderLoggedUser(messageEntry)) {

			decryptedContentString =

					messageEntry.getContent();
			} else {
			decryptedContentString =
			EncryptionHelper.decrypt(
				messageEntry.getContentEncrypted(),
				KeyStoreUtil.getPrivateKeyFromFile(mContext, currentUser)
			);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
*/
            decryptedContentString = messageEntry.getContent();

            ((TextViewHolder) holder).bind(
                  decryptedContentString,
                  timeForm,
                  messageEntry.getSenderId(),
                  currentUser, isNewDay(messageEntries, position), time, shouldShowTime(position), shouldShowProfilePicture(position)
               );
         } else if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
            ((ImageViewHolder) holder).bind(
                  messageEntry.getContentEncrypted(),
                  mContext,
                  () ->
                     messageApiHelper.reloadMessages(
                        mContext,
                        messageEntry.getConversationId(),
                        this,
                        null
                     )
               );
         }
   }

   @Override
   public int getItemCount() {
      return messageEntries.size();
   }

   public void setMessageEntries(List<MessageEntry> messageEntries) {
      this.messageEntries = messageEntries;
      notifyDataSetChanged();
   }

   public void setUsers(List<User> users) {
      notifyDataSetChanged();
   }

   private boolean isSenderLoggedUser(MessageEntry messageEntry) {
      return currentUser.getUserId().equals(messageEntry.getSenderId());
   }

   private boolean isNewDay(List<MessageEntry> messageEntries, int position) {
      if (position > 0) {
         // Get the timestamps of the current and previous messages
         long currentTimestamp = messageEntries.get(position).getTimestamp();
         long previousTimestamp = messageEntries.get(position - 1).getTimestamp();

         // Convert timestamps to Date objects
         Date currentDate = new Date(currentTimestamp);
         Date previousDate = new Date(previousTimestamp);

         // Format to get only the date (year, month, and day)
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         String currentDateStr = dateFormat.format(currentDate);
         String previousDateStr = dateFormat.format(previousDate);

         // If the dates are different, return true (new day)
         return !currentDateStr.equals(previousDateStr);
      } else {
         // If it's the first message, it's a new day
         return true;
      }
   }

   private boolean shouldShowTime(int position) {
      if (position == 0) return true; // Always show time for the first message
      long currentTime = messageEntries.get(position).getTimestamp();
      long previousTime = messageEntries.get(position - 1).getTimestamp();
      return (currentTime - previousTime) > (10 * 60 * 1000); // 10 minutes in milliseconds
   }

   private boolean shouldShowProfilePicture(int position) {
      if (position == 0) return true; // Always show picture for the first message
      Long currentSender = messageEntries.get(position).getSenderId();
      Long previousSender = messageEntries.get(position - 1).getSenderId();
      return !currentSender.equals(previousSender);
   }
}
