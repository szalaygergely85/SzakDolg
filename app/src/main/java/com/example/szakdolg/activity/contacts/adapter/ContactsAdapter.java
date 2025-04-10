package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdolg.DTO.ContactsDTO;
import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.profile.ProfileActivity;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.activity.contacts.constans.ContactsConstans;
import com.example.szakdolg.activity.profile.ProfileConstants;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter
   extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final int TYPE_HEADER = 0;
   private static final int TYPE_ITEM = 1;
   private final Context context;

   private final User currentUser;

   private List<Object> contactsDTOList = new ArrayList<>();

   private List<Long> selectedUsers = new ArrayList<>();

   private String actionId;

   private ContactService contactService;

   private ConversationService conversationService;

   public ContactsAdapter(
      Context context,
      User currentUser,
      String actionId
   ) {
      this.context = context;
      this.currentUser = currentUser;
      selectedUsers.add(currentUser.getUserId());
      this.actionId = actionId;
      this.contactService = new ContactService(context, currentUser);
      this.conversationService = new ConversationService(context, currentUser);
   }

   @Override
   public int getItemViewType(int position) {
      if (contactsDTOList.get(position) instanceof String) {
         return TYPE_HEADER;
      } else {
         return TYPE_ITEM;
      }
   }

   @NonNull
   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      if (viewType == TYPE_HEADER) {
         View view = inflater.inflate(
            R.layout.item_contact_header,
            parent,
            false
         );
         return new HeaderViewHolder(view);
      } else {
         View view = inflater.inflate(
            R.layout.item_contact_user,
            parent,
            false
         );
         return new ContactViewHolder(view);
      }
   }

   @Override
   public void onBindViewHolder(
      @NonNull RecyclerView.ViewHolder holder,
      int position
   ) {

       int i = holder.getAdapterPosition();
      if (holder instanceof HeaderViewHolder) {
         ((HeaderViewHolder) holder).headerText.setText(
               (String) contactsDTOList.get(i)
            );
      } else {
         ContactsDTO contactsDTO = (ContactsDTO) contactsDTOList.get(i);
         User user = contactsDTO.getUser();
         Contact contact = contactsDTO.getContact();
         String displayName = user.getDisplayName();
         if (displayName != null) {
            ((ContactViewHolder) holder).txtName.setText(displayName);
         }

         String imageUrl = ImageUtil.buildProfileImageUrl(
                 user.getUserId()
         );
         Glide
                 .with(context)
                 .load(imageUrl)
                 .placeholder(R.drawable.ic_blank_profile)
                 .error(R.drawable.ic_blank_profile)
                 .into(((ContactViewHolder) holder).profileImageView);

         //VIEW ACTION

         if (actionId.equals(ContactsConstans.ACTION_VIEW)) {
            ((ContactViewHolder) holder).linearLayout.setOnLongClickListener(v -> {
                     // Give haptic feedback
                     v.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS
                     );

                     // Show popup menu
                     PopupMenu popup = new PopupMenu(v.getContext(), v);
                     popup
                        .getMenuInflater()
                        .inflate(R.menu.menu_contact_action, popup.getMenu());

                     popup.setOnMenuItemClickListener(item -> {
                        Intent intent;
                        switch (item.getItemId()) {
                           case R.id.action_view_profile:
                              startProfileActivity(user, contact);
                              return true;
                           case R.id.action_start_chat:
                              List<Long> participants = new ArrayList<>();
                              participants.add(currentUser.getUserId());
                              participants.add(user.getUserId());

                              conversationService.addConversationByUserId(
                                 participants,
                                 new ConversationService.ConversationCallback<
                                    ConversationDTO
                                 >() {
                                    @Override
                                    public void onSuccess(
                                       ConversationDTO data
                                    ) {
                                       Intent intent = new Intent(
                                          context,
                                          ChatActivity.class
                                       );
                                       intent.putExtra(
                                          IntentConstants.CONVERSATION_ID,
                                          data
                                             .getConversation()
                                             .getConversationId()
                                       );
                                       intent.putExtra(
                                          IntentConstants.CONVERSATION_DTO,
                                          data
                                       );
                                       context.startActivity(intent);
                                    }

                                    @Override
                                    public void onError(Throwable t) {}
                                 }
                              );
                              return true;
                           case R.id.action_remove:
                              contactService.deleteContact(
                                 contact,
                                 new ContactService.ContactCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void data) {
                                       Toast
                                          .makeText(
                                             v.getContext(),
                                             "Contact removed",
                                             Toast.LENGTH_SHORT
                                          )
                                          .show();
                                       contactsDTOList.remove(contactsDTO);
                                       notifyItemChanged(i);
                                    }

                                    @Override
                                    public void onError(Throwable t) {}
                                 }
                              );

                              return true;
                        }
                        return false;
                     });

                     popup.show();
                     return true;
                  }
               );

            ((ContactViewHolder) holder).linearLayout.setOnClickListener(
                  new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
startProfileActivity(user, contact);
                     }
                  }
               );
         }

         //SELECT ACTION

         if (actionId.equals(ContactsConstans.ACTION_SELECT)) {
            ((ContactViewHolder) holder).linearLayout.setOnClickListener(
                  new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                        if (selectedUsers.contains(user.getUserId())) {
                           selectedUsers.remove(user.getUserId());
                           ((ContactViewHolder) holder).checkImageView.setVisibility(
                                 View.INVISIBLE
                              );
                        } else {
                           selectedUsers.add(user.getUserId());
                           ((ContactViewHolder) holder).checkImageView.setVisibility(
                                 View.VISIBLE
                              );
                        }
                     }
                  }
               );
         }
      }
   }

   private void startProfileActivity(User user, Contact contact) {

      Intent intent = new Intent(
              context,
              ProfileActivity.class
      );
      intent.putExtra(
              IntentConstants.PROFILE_ACTION,
              ProfileConstants.VIEW_CONTACT
      );
      intent.putExtra(IntentConstants.CONTACT, contact);
      intent.putExtra(IntentConstants.USER, user);
      context.startActivity(intent);
   }

   public List<Long> getSelectedUsers() {
      return selectedUsers;
   }

   public void setUsers(List<Object> usersList) {
      this.contactsDTOList = usersList;
      notifyDataSetChanged();
   }

   @Override
   public int getItemCount() {
      return contactsDTOList.size();
   }

   static class HeaderViewHolder extends RecyclerView.ViewHolder {

      TextView headerText;

      HeaderViewHolder(View itemView) {
         super(itemView);
         headerText = itemView.findViewById(R.id.headerText);
      }
   }

   static class ContactViewHolder extends RecyclerView.ViewHolder {

      private final ImageView checkImageView;
      private final ImageView profileImageView;
      private final TextView txtName;
      private final LinearLayout linearLayout;

      ContactViewHolder(View itemView) {
         super(itemView);
         txtName = itemView.findViewById(R.id.contactSelectName);
         linearLayout = itemView.findViewById(R.id.contactSelect_ll);
         checkImageView = itemView.findViewById(R.id.imageView2);
         profileImageView = itemView.findViewById(R.id.contactSelectImage);
      }
   }
}
