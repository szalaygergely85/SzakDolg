package com.example.szakdolg.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.viewholder.ImageViewHolder;
import com.example.szakdolg.activity.chat.viewholder.TextViewHolder;
import com.example.szakdolg.activity.contacts.adapter.SelectContactsAdapter;
import com.example.szakdolg.models.message.api.MessageApiHelper;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final int TYPE_DATE = 0;
   private static final int TYPE_IN = 1;
   private static final int TYPE_OUT = 2;

   private final Context mContext;
   private List<Object> messageEntries = new ArrayList<>();

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
/*
         messageEntries.sort(
            Comparator.comparingLong(MessageEntry::getTimestamp)
         );*/
         notifyDataSetChanged();
         chatRecView.scrollToPosition(getItemCount() - 1);
      }
   }

   @Override
   public int getItemViewType(int position) {
      if (messageEntries.get(position) instanceof String) {
         return TYPE_DATE;
      } else {
         MessageEntry messageEntry = (MessageEntry) messageEntries.get(position);
         if(Objects.equals(messageEntry.getSenderId(), currentUser.getUserId())){
            return TYPE_OUT;
         }else {
            return TYPE_IN;
         }

      }
   }

   @NonNull
   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      if (viewType == TYPE_DATE) {
         View view = inflater.inflate(R.layout.item_chat_date, parent, false);
         return new ChatAdapter.DateViewHolder(view);
      }
      if (viewType == TYPE_IN){
         View view = inflater.inflate(R.layout.item_chat_inbound, parent, false);
         return new ChatAdapter.InboundTextViewHolder(view);
      }
      if(viewType == TYPE_OUT) {
         View view = inflater.inflate(R.layout.item_chat_outbound, parent, false);
         return new ChatAdapter.OutboundTextViewHolder(view);
      }
      throw new IllegalArgumentException("Invalid viewType: " + viewType);
   }

   @Override
   public void onBindViewHolder(
      @NonNull RecyclerView.ViewHolder holder,
      int position
   ) {

      if (holder instanceof ChatAdapter.DateViewHolder) {
         ((DateViewHolder) holder).dateTextView.setText((String)messageEntries.get(position));
      }else {
         MessageEntry messageEntry = (MessageEntry) messageEntries.get(position);

         if (holder instanceof ChatAdapter.InboundTextViewHolder) {

            ((InboundTextViewHolder) holder).txtText.setText(messageEntry.getContent());

         }

         if (holder instanceof ChatAdapter.OutboundTextViewHolder) {

            ((OutboundTextViewHolder) holder).txtTextFrMe.setText(messageEntry.getContent());

         }
      }
      /*
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
      }*/
   }

   @Override
   public int getItemCount() {
      return messageEntries.size();
   }

   public void setMessageEntries(List<Object> messageEntries) {
      if (messageEntries != null) {

      }
      this.messageEntries = messageEntries;
      notifyDataSetChanged();
      chatRecView.scrollToPosition(getItemCount() - 1);
   }


   static class DateViewHolder extends RecyclerView.ViewHolder {
      private final TextView dateTextView;

      DateViewHolder(View itemView) {
         super(itemView);
         dateTextView = itemView.findViewById(R.id.dateTextView);
      }
   }

   static class InboundTextViewHolder extends RecyclerView.ViewHolder {
      private final TextView txtText;
      private final TextView txtTimeIn;

      private final ImageView imageView;

      InboundTextViewHolder(View itemView) {
         super(itemView);
         txtText = itemView.findViewById(R.id.chatText);
         txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
         imageView = itemView.findViewById(R.id.profilePicIn);
      }
   }

   static class OutboundTextViewHolder extends RecyclerView.ViewHolder {
      private final TextView txtTimeOut;
      private final TextView txtTextFrMe;

      OutboundTextViewHolder(View itemView) {
         super(itemView);
         txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
         txtTimeOut = itemView.findViewById(R.id.chatTextTimeOut);
      }
   }


}
