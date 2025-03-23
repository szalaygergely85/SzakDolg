package com.example.szakdolg.activity.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.conversation.api.ConversationApiHelper;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.message.MessageCoordinatorService;
import com.example.szakdolg.models.message.MessageService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

   private List<ConversationDTO> conversationDTOList = new ArrayList<>();
   private List<User> users = new ArrayList<>();
   private final Context context;
   private  Conversation conversation;
   private final User currentUser;

   private final MessageService messageService;

   ConversationApiHelper conversationApiHelper;

   MainAdapterHelper mainAdapterHelper;
   RecyclerView mainRecView;

   public MainAdapter(
      Context mContext,
      User currentUser,
      RecyclerView mainRecView
   ) {
      this.context = mContext;
      this.currentUser = currentUser;
      this.messageService =
      new MessageService(context, currentUser);
      this.mainAdapterHelper = new MainAdapterHelper(currentUser, mContext);
      this.conversationApiHelper =
      new ConversationApiHelper(mContext, currentUser);
      this.mainRecView = mainRecView;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.item_main_list, parent, false);
      ViewHolder holder = new ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(
      @NonNull ViewHolder holder,
      @SuppressLint("RecyclerView") int position
   ) {
      ConversationDTO conversationDTO = conversationDTOList.get(position);

       conversation = conversationDTO.getConversation();

      users =  conversationDTO.getUsers();

      Long conversationId = conversation.getConversationId();

      messageService.getLatestMessageEntry(conversationId, new MessageService.MessageCallback<MessageEntry>() {
         @Override
         public void onSuccess(MessageEntry messageEntry) {

            holder.txtName.setText(conversation.getConversationName());



            if (
                    messageEntry == null ||
                            ((messageEntry.getContentEncrypted() == null) &&
                                    messageEntry.getContent() == null)
            ) {
               holder.itemView.setVisibility(View.GONE);
               return;
            }

            int count = mainAdapterHelper.getCountByNotReadMsg(conversationId);
            if (count == 0) {
               holder.txtNotRead.setVisibility(View.GONE);
            } else {
               holder.txtNotRead.setText(String.valueOf(count));
            }


            if ( users== null) {
               holder.itemView.setVisibility(View.GONE);
               return;
            }

            holder.txtName.setText(
                    mainAdapterHelper.getConversationTitle(conversation, users)
            );

            holder.txtMessage.setText(mainAdapterHelper.getContent(messageEntry));

            holder.txtTime.setText(
                    mainAdapterHelper.getTime(messageEntry.getTimestamp())
            );

            mainAdapterHelper.setImageView(users, holder.image);

            holder.parent.setOnClickListener(
                    new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                          Intent intent = new Intent(context, ChatActivity.class);
                          intent.putExtra(IntentConstants.CONVERSATION_DTO, conversationDTO);
                          intent.putExtra(IntentConstants.CONVERSATION_ID, conversationId);
                          context.startActivity(intent);

                       }
                    }
            );




         }

         @Override
         public void onError(Throwable t) {

         }
      });




   }

   @Override
   public int getItemCount() {
      return conversationDTOList.size();
   }

   public void setConversationList(List<ConversationDTO> conversationDTOList) {
      this.conversationDTOList = conversationDTOList;
      notifyDataSetChanged();
   }


   public class ViewHolder extends RecyclerView.ViewHolder {

      private final TextView txtName;
      private final TextView txtMessage;
      private final TextView txtTime;
      private final RelativeLayout parent;
      private final ImageView image;
      private final TextView txtNotRead;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         image = itemView.findViewById(R.id.mesBrdImage);
         txtName = itemView.findViewById(R.id.mesBrdName);
         txtMessage = itemView.findViewById(R.id.mesBrdMessage);
         parent = itemView.findViewById(R.id.parent);
         txtTime = itemView.findViewById(R.id.main_item_last_msg_time);
         txtNotRead = itemView.findViewById(R.id.main_item_not_read);
      }
   }
}
