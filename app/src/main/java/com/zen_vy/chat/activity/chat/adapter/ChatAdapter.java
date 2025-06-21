package com.zen_vy.chat.activity.chat.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.chat.activity.ChatActivityHelper;
import com.zen_vy.chat.activity.image.FullscreenImageActivity;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final int TYPE_DATE = 0;
   private static final int TYPE_IN = 1;
   private static final int TYPE_OUT = 2;

   private static final int TYPE_IN_IMAGE = 3;
   private static final int TYPE_OUT_IMAGE = 4;

   private final Context mContext;
   private List<Object> messageEntries = new ArrayList<>();

   private ArrayList<String> imageUrls = new ArrayList<>();

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
         notifyItemRangeChanged(getItemCount() - 2, 2);
         chatRecView.scrollToPosition(getItemCount() - 1);
      }
   }

   public void setImageUrls(ArrayList<String> imageUrls) {
      this.imageUrls = imageUrls;
   }

   @Override
   public int getItemViewType(int position) {
      if (messageEntries.get(position) instanceof String) {
         return TYPE_DATE;
      } else {
         MessageEntry messageEntry = (MessageEntry) messageEntries.get(
            position
         );

         if (MessageTypeConstants.MESSAGE == messageEntry.getType()) {
            if (
               Objects.equals(
                  messageEntry.getSenderId(),
                  currentUser.getUserId()
               )
            ) {
               return TYPE_OUT;
            } else {
               return TYPE_IN;
            }
         }
         if (MessageTypeConstants.IMAGE == messageEntry.getType()) {
            if (
               Objects.equals(
                  messageEntry.getSenderId(),
                  currentUser.getUserId()
               )
            ) {
               return TYPE_OUT_IMAGE;
            } else {
               return TYPE_IN_IMAGE;
            }
         }
      }
      return -1;
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

      if (viewType == TYPE_OUT_IMAGE) {
         View view = inflater.inflate(
            R.layout.item_chat_outbound_image,
            parent,
            false
         );
         return new ChatAdapter.OutBoundImageHolder(view);
      }

      if (viewType == TYPE_IN_IMAGE) {
         View view = inflater.inflate(
            R.layout.item_chat_inbound_image,
            parent,
            false
         );
         return new ChatAdapter.InBoundImageHolder(view);
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

         String imageProfileUrl = ImageUtil.buildProfileImageUrl(
            messageEntry.getSenderId()
         );

         GlideUrl glideProfileUrl = new GlideUrl(
            imageProfileUrl,
            new LazyHeaders.Builder()
               .addHeader("Authorization", currentUser.getToken())
               .build()
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

                  Glide
                     .with(mContext)
                     .load(glideProfileUrl)
                     .diskCacheStrategy(DiskCacheStrategy.ALL)
                     .placeholder(R.drawable.ic_blank_profile)
                     .error(R.drawable.ic_blank_profile)
                     .into(((InboundTextViewHolder) holder).imageView);
               }
               params.setMargins(
                  marginInPx,
                  params.topMargin,
                  params.rightMargin,
                  params.bottomMargin
               );
            } else {
               Glide
                  .with(mContext)
                  .load(glideProfileUrl)
                  .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .placeholder(R.drawable.ic_blank_profile)
                  .error(R.drawable.ic_blank_profile)
                  .into(((InboundTextViewHolder) holder).imageView);
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

         if (holder instanceof ChatAdapter.InBoundImageHolder) {
            String imageUrl = ImageUtil.buildImageUrl(
               messageEntry.getContent()
            );

            GlideUrl glideUrl = ImageUtil.getGlideUrlWithTokenHeader(
               imageUrl,
               currentUser.getToken()
            );

            Glide
               .with(mContext)
               .load(glideUrl)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .placeholder(R.drawable.ic_blank_profile)
               .error(R.drawable.ic_blank_profile)
               .into(((InBoundImageHolder) holder).inImageView);

            ((InBoundImageHolder) holder).inImageView.setOnClickListener(
                  new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                        Intent intent = new Intent(
                           mContext,
                           FullscreenImageActivity.class
                        );
                        intent.putStringArrayListExtra(
                           IntentConstants.IMAGES,
                           imageUrls
                        );
                        intent.putExtra(
                           IntentConstants.IMAGE,
                           messageEntry.getContent()
                        );
                        mContext.startActivity(intent);
                     }
                  }
               );
         }
         if (holder instanceof ChatAdapter.OutBoundImageHolder) {
            String imageUrl = ImageUtil.buildImageUrl(
               messageEntry.getContent()
            );

            GlideUrl glideUrl = ImageUtil.getGlideUrlWithTokenHeader(
               imageUrl,
               currentUser.getToken()
            );

            Glide
               .with(mContext)
               .load(glideUrl)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .placeholder(R.drawable.ic_blank_profile)
               .error(R.drawable.ic_blank_profile)
               .into(((OutBoundImageHolder) holder).outImageView);

            ((OutBoundImageHolder) holder).outImageView.setOnClickListener(
                  new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                        Intent intent = new Intent(
                           mContext,
                           FullscreenImageActivity.class
                        );
                        intent.putStringArrayListExtra(
                           IntentConstants.IMAGES,
                           imageUrls
                        );
                        intent.putExtra(
                           IntentConstants.IMAGE,
                           messageEntry.getContent()
                        );
                        mContext.startActivity(intent);
                     }
                  }
               );
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

   static class OutBoundImageHolder extends RecyclerView.ViewHolder {

      private final ImageView outImageView;

      OutBoundImageHolder(View itemView) {
         super(itemView);
         outImageView = itemView.findViewById(R.id.sentImageView);
      }
   }

   static class InBoundImageHolder extends RecyclerView.ViewHolder {

      private final ImageView inImageView;

      InBoundImageHolder(View itemView) {
         super(itemView);
         inImageView = itemView.findViewById(R.id.receivedImageView);
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
