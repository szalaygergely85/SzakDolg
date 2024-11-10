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
private ChatAdapterHelper chatAdapterHelper;
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
      chatAdapterHelper = new ChatAdapterHelper(messageEntries);

      MessageEntry messageEntry = messageEntries.get(
         holder.getAdapterPosition()
      );


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
            Long timeStamp = messageEntry.getTimestamp();

            String timeForm = chatAdapterHelper.getTime(timeStamp);
            boolean isNewDay = chatAdapterHelper.isNewDay(position);
            boolean shouldShowTime = chatAdapterHelper.shouldShowTime(position);
            boolean shouldShowProfilePicture = chatAdapterHelper.shouldShowProfilePicture(position);

            ((TextViewHolder) holder).bind(
                  decryptedContentString,
                  timeForm,
                  messageEntry.getSenderId(),
                  currentUser, isNewDay, timeStamp, shouldShowTime, shouldShowProfilePicture
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


}
