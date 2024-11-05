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
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;
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
         String decryptedContentString = null;
         try {
            if (isSenderLoggedUser(messageEntry)) {
               decryptedContentString =
               EncryptionHelper.decrypt(
                  messageEntry.getContentSenderVersion(),
                  KeyStoreUtil.getPrivateKeyFromFile(mContext, currentUser)
               );
            } else {
               decryptedContentString =
               EncryptionHelper.decrypt(
                  messageEntry.getContent(),
                  KeyStoreUtil.getPrivateKeyFromFile(mContext, currentUser)
               );
            }
         } catch (Exception e) {
            throw new RuntimeException(e);
         }

         ((TextViewHolder) holder).bind(
               decryptedContentString,
               timeForm,
               messageEntry.getSenderId(),
               currentUser
            );
      } else if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
         ((ImageViewHolder) holder).bind(
               messageEntry.getContent(),
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
