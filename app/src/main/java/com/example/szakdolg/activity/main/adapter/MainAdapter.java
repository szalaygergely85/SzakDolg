package com.example.szakdolg.activity.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.message.MessageCoordinatorService;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter
   extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
   private List<Conversation> conversationList = new ArrayList<>();
   private final Context context;
   private User currentUser;
   private String _token;
   private UserDatabaseUtil userDatabaseUtil;
   private MessageCoordinatorService messageCoordinatorService;

   private ConversationDatabaseUtil conversationDatabaseUtil;
   ConversationApiHelper conversationApiHelper = new ConversationApiHelper();

   MainAdapterHelper mainAdapterHelper;

   public MainAdapter(
      Context mContext,
      String token,
      User currentUser
   ) {
      this.context = mContext;
      this.currentUser = currentUser;

      this.messageCoordinatorService = new MessageCoordinatorService(context, currentUser);
      this.mainAdapterHelper =
      new MainAdapterHelper(currentUser, mContext);
      this.userDatabaseUtil = new UserDatabaseUtil(mContext, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(mContext, currentUser);
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

      //TODO itt tartok
      MessageEntry messageEntry = messageCoordinatorService.getLatestMessageEntry(
         conversation.getConversationId()
      );

      if (messageEntry.getContent() != null) {
         List<ConversationParticipant> participants =
            conversationDatabaseUtil.getParticipantsByConversationId(
               conversation.getConversationId()
            );
         User participant = userDatabaseUtil.getUserById(
            participants.get(0).getUserId()
         );

         if (messageEntry.isRead() || isSenderLoggedUser(messageEntry)) {
            holder.txtMessage.setTypeface(null, Typeface.NORMAL);
            holder.txtName.setTypeface(null, Typeface.NORMAL);
         } else {
            holder.txtMessage.setTypeface(null, Typeface.BOLD);
            holder.txtName.setTypeface(null, Typeface.BOLD);
         }
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
               holder.txtMessage.setText(decryptedContentString);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
         if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
            holder.txtMessage.setText("Image received from:");
         }
         holder.txtName.setText(participant.getDisplayName());

         mainAdapterHelper.setImageView(
            participant.getUserId(),
            context,
            holder.image
         );

         holder.parent.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View view) {
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
