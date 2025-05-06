package com.example.szakdolg.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.activity.ChatActivityHelper;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final int TYPE_DATE = 0;
   private static final int TYPE_IN = 1;
   private static final int TYPE_OUT = 2;

   private final Context mContext;
   private List<Object> messageEntries = new ArrayList<>();


   private final User currentUser;

   private RecyclerView chatRecView;
   private ChatActivityHelper chatActivityHelper;

   public ChatAdapter(
      Context mContext,
      User user,
      RecyclerView chatRecView,
      ChatActivityHelper chatActivityHelper
   ) {
      this.mContext = mContext;
      this.currentUser = user;
      this.chatRecView = chatRecView;
      this.chatActivityHelper = chatActivityHelper;
   }

   public void addMessage(MessageEntry messageEntry) {
      if (messageEntry != null) {
         messageEntries.add(getItemCount(), messageEntry);
         notifyItemInserted(getItemCount() - 1);
         notifyItemChanged(getItemCount() - 2);
         chatRecView.scrollToPosition(getItemCount() - 1);
      }
   }

   @Override
   public int getItemViewType(int position) {
      if (messageEntries.get(position) instanceof String) {
         return TYPE_DATE;
      } else {
         MessageEntry messageEntry = (MessageEntry) messageEntries.get(
            position
         );
         if (
            Objects.equals(messageEntry.getSenderId(), currentUser.getUserId())
         ) {
            return TYPE_OUT;
         } else {
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
      if (viewType == TYPE_IN) {
         View view = inflater.inflate(
            R.layout.item_chat_inbound,
            parent,
            false
         );
         return new ChatAdapter.InboundTextViewHolder(view);
      }
      if (viewType == TYPE_OUT) {
         View view = inflater.inflate(
            R.layout.item_chat_outbound,
            parent,
            false
         );
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
         ((DateViewHolder) holder).dateTextView.setText(
               (String) messageEntries.get(position)
            );
      } else {
         MessageEntry messageEntry = (MessageEntry) messageEntries.get(
            position
         );

         if (holder instanceof ChatAdapter.InboundTextViewHolder) {
            ((InboundTextViewHolder) holder).txtText.setText(
                  messageEntry.getContent()
               );
            ((InboundTextViewHolder) holder).txtTimeIn.setText(
                  chatActivityHelper.getTime(messageEntry.getTimestamp())
               );

            ViewGroup.MarginLayoutParams params =
               (ViewGroup.MarginLayoutParams) ((InboundTextViewHolder) holder).linearLayoutIn.getLayoutParams();
            int marginInPx = 0;

            if (position < getItemCount() - 1) {
               if (getItemViewType(position + 1) == TYPE_IN) {
                  ((InboundTextViewHolder) holder).imageView.setVisibility(
                        View.GONE
                     );
                  ((InboundTextViewHolder) holder).txtTimeIn.setVisibility(
                        View.GONE
                     );

                  int marginInDp = 40; // You can set any margin in dp you want
                  float density = holder.itemView
                     .getContext()
                     .getResources()
                     .getDisplayMetrics()
                     .density;
                  marginInPx = (int) (marginInDp * density);

                  ((InboundTextViewHolder) holder).linearLayoutIn.setLayoutParams(
                        params
                     );
                  ((InboundTextViewHolder) holder).txtText.setBackground(
                        ContextCompat.getDrawable(
                           mContext,
                           R.drawable.bg_chat_white
                        )
                     );
               } else {
                  ((InboundTextViewHolder) holder).txtText.setBackground(
                        ContextCompat.getDrawable(
                           mContext,
                           R.drawable.bg_chat_one_sided_white
                        )
                     );

                  ((InboundTextViewHolder) holder).imageView.setVisibility(
                        View.VISIBLE
                     );
                  ((InboundTextViewHolder) holder).txtTimeIn.setVisibility(
                        View.VISIBLE
                     );

                  String imageUrl = ImageUtil.buildProfileImageUrl(
                     messageEntry.getSenderId()
                  );
                  if (imageUrl != null) {
                     Glide
                             .with(mContext)
                             .load(imageUrl)
                             .diskCacheStrategy(DiskCacheStrategy.ALL)
                             .placeholder(R.drawable.ic_blank_profile)
                             .error(R.drawable.ic_blank_profile)
                             .into( ((InboundTextViewHolder) holder).imageView);
                  } else {
                     ((InboundTextViewHolder) holder).imageView.setImageResource(R.drawable.ic_blank_profile);
                  }
               }
               params.setMargins(
                  marginInPx,
                  params.topMargin,
                  params.rightMargin,
                  params.bottomMargin
               );
            }else {
               String imageUrl = ImageUtil.buildProfileImageUrl(
                       messageEntry.getSenderId()
               );
               if (imageUrl != null) {
                  Glide
                          .with(mContext)
                          .load(imageUrl)
                          .diskCacheStrategy(DiskCacheStrategy.ALL)
                          .placeholder(R.drawable.ic_blank_profile)
                          .error(R.drawable.ic_blank_profile)
                          .into( ((InboundTextViewHolder) holder).imageView);
               }
            }
         }
         if (holder instanceof ChatAdapter.OutboundTextViewHolder) {
            ((OutboundTextViewHolder) holder).txtTextFrMe.setText(
                  messageEntry.getContent()
               );

            ((OutboundTextViewHolder) holder).txtTimeOut.setText(
                  chatActivityHelper.getTime(messageEntry.getTimestamp())
               );

            if (position < getItemCount() - 1) {
               if (getItemViewType(position + 1) == TYPE_OUT) {
                  ((OutboundTextViewHolder) holder).txtTimeOut.setVisibility(
                        View.GONE
                     );
                  ((OutboundTextViewHolder) holder).txtTextFrMe.setBackground(
                        ContextCompat.getDrawable(
                           mContext,
                           R.drawable.bg_chat_grey
                        )
                     );
               } else {
                  ((OutboundTextViewHolder) holder).txtTimeOut.setVisibility(
                        View.VISIBLE
                     );
                  ((OutboundTextViewHolder) holder).txtTextFrMe.setBackground(
                        ContextCompat.getDrawable(
                           mContext,
                           R.drawable.bg_chat_one_sided_grey
                        )
                     );
               }
            }
         }
      }
   }

   @Override
   public int getItemCount() {
      return messageEntries.size();
   }

   public void setMessageEntries(List<Object> messageEntries) {
      if (messageEntries != null) {
         this.messageEntries = messageEntries;
         notifyDataSetChanged();
         chatRecView.scrollToPosition(getItemCount() - 1);
      }

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
      private final LinearLayout linearLayoutIn;

      InboundTextViewHolder(View itemView) {
         super(itemView);
         txtText = itemView.findViewById(R.id.chatText);
         txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
         imageView = itemView.findViewById(R.id.profilePicIn);
         linearLayoutIn = itemView.findViewById(R.id.chat_in_ll);
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
