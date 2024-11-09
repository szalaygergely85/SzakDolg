package com.example.szakdolg.activity.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.conversation.api.ConversationApiHelper;
import com.example.szakdolg.model.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.message.MessageCoordinatorService;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

   private List<Conversation> conversationList = new ArrayList<>();
   private final Context context;
   private User currentUser;
   private String _token;
   private UserDatabaseUtil userDatabaseUtil;
   private MessageCoordinatorService messageCoordinatorService;

   private ConversationDatabaseUtil conversationDatabaseUtil;
   ConversationApiHelper conversationApiHelper;

   MainAdapterHelper mainAdapterHelper;

   public MainAdapter(Context mContext, User currentUser) {
      this.context = mContext;
      this.currentUser = currentUser;

      this.messageCoordinatorService =
      new MessageCoordinatorService(context, currentUser);
      this.mainAdapterHelper = new MainAdapterHelper(currentUser, mContext);
      this.userDatabaseUtil = new UserDatabaseUtil(mContext, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(mContext, currentUser);
      this.conversationApiHelper =
      new ConversationApiHelper(mContext, currentUser);
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.main_item, parent, false);
      ViewHolder holder = new ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(
      @NonNull ViewHolder holder,
      @SuppressLint("RecyclerView") int position
   ) {
      Conversation conversation = conversationList.get(position);

      holder.txtName.setText(conversation.getConversationName());
      Long conversationId = conversation.getConversationId();

      MessageEntry messageEntry =
         messageCoordinatorService.getLatestMessageEntry(conversationId);

      if (messageEntry == null || messageEntry.getContentEncrypted() == null) {
         return;
      }

      List<User> participants = mainAdapterHelper.getParticipantUser(
         conversationId
      );

      holder.txtName.setText(
         mainAdapterHelper.getConversationTitle(conversationId, participants)
      );

      if (messageEntry.isRead() || isSenderLoggedUser(messageEntry)) {
         holder.txtMessage.setTypeface(null, Typeface.NORMAL);
      } else {
         holder.txtMessage.setTypeface(null, Typeface.BOLD);
      }

      holder.txtMessage.setText(mainAdapterHelper.getContent(messageEntry));

      holder.txtTime.setText(
         mainAdapterHelper.getTime(messageEntry.getTimestamp())
      );

      mainAdapterHelper.setImageView(participants, holder.image);

      holder.parent.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(context, ChatActivity.class);
               intent.putExtra(IntentConstants.CONVERSATION_ID, conversationId);
               context.startActivity(intent);

               conversationApiHelper.openConversation(
                  conversation.getConversationId(),
                  context,
                  currentUser,
                  _token
               );
            }
         }
      );
   }

   @Override
   public int getItemCount() {
      return conversationList.size();
   }

   public void setConversationList(List<Conversation> conversationList) {
      this.conversationList = conversationList;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final TextView txtName;
      private final TextView txtMessage;
      private final TextView txtTime;
      private final RelativeLayout parent;
      private final ImageView image;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         image = itemView.findViewById(R.id.mesBrdImage);
         txtName = itemView.findViewById(R.id.mesBrdName);
         txtMessage = itemView.findViewById(R.id.mesBrdMessage);
         parent = itemView.findViewById(R.id.parent);
         txtTime = itemView.findViewById(R.id.mesBrdTime);
      }
   }

   private boolean isSenderLoggedUser(MessageEntry messageEntry) {
      return currentUser.getUserId().equals(messageEntry.getSenderId());
   }
}
