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
import com.example.szakdolg.models.message.api.MessageApiHelper;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private final Context mContext;
   private List<MessageEntry> messageEntries = new ArrayList<>();

   private MessageApiHelper messageApiHelper;
   private final User currentUser;

   private RecyclerView chatRecView;
   private ChatAdapterHelper chatAdapterHelper;

   public ChatAdapter(Context mContext, User user, RecyclerView chatRecView) {
      this.mContext = mContext;
      this.currentUser = user;
      this.messageApiHelper = new MessageApiHelper(mContext, user);
      this.chatRecView = chatRecView;
   }

   public void addMessage(MessageEntry messageEntry) {
      if (messageEntry != null) {
         messageEntries.add(messageEntry);

         messageEntries.sort(
            Comparator.comparingLong(MessageEntry::getTimestamp)
         );
         notifyDataSetChanged();
         chatRecView.scrollToPosition(getItemCount() - 1);
      }
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
            .inflate(R.layout.item_chat_list, parent, false);
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

         ((TextViewHolder) holder).bind(
               decryptedContentString,
               chatAdapterHelper.getTime(messageEntry.getTimestamp()),
               messageEntry.getSenderId(),
               currentUser,
               chatAdapterHelper.isNewDay(position),
               messageEntry.getTimestamp(),
               chatAdapterHelper.shouldShowTime(position),
               chatAdapterHelper.shouldShowProfilePicture(position),
               mContext
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
      if (messageEntries != null) {
         // Sort the list by timestamp
         messageEntries.sort(
            Comparator.comparingLong(MessageEntry::getTimestamp)
         );
      }
      this.messageEntries = messageEntries;
      notifyDataSetChanged();
      chatRecView.scrollToPosition(getItemCount() - 1);
   }

   public void setUsers(List<User> users) {
      notifyDataSetChanged();
   }
}
