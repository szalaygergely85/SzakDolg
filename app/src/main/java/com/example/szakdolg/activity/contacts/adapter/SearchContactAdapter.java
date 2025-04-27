package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.szakdolg.R;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.user.entity.User;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class SearchContactAdapter
   extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder> {

   private final Context context;

   private User currentUser;
   private List<User> userList = new ArrayList<>();
   private ContactService contactService;

   public SearchContactAdapter(Context context, User currentUser) {
      this.currentUser = currentUser;
      this.context = context;
      this.contactService = new ContactService(context, currentUser);
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.item_contact_search, parent, false);
      SearchContactAdapter.ViewHolder holder =
         new SearchContactAdapter.ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User user = userList.get(holder.getAdapterPosition());

      holder.txtName.setText(user.getDisplayName());

      String imageUrl = ImageUtil.buildProfileImageUrl(user.getUserId());
      Glide
         .with(context)
         .load(imageUrl)
         .placeholder(R.drawable.ic_blank_profile)
              .diskCacheStrategy(DiskCacheStrategy.ALL)
         .error(R.drawable.ic_blank_profile)
         .into(holder.imageView);

      contactService.isContact(
         user.getUserId(),
         currentUser.getToken(),
         new ContactService.ContactCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean found) {
               if (found) {
                  holder.btnAdd.setIconResource(R.drawable.ic_check);
               }
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      holder.btnAdd.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               contactService.addContact(
                  new Contact(currentUser.getUserId(), user.getUserId()),
                  currentUser.getToken(),
                  new ContactService.ContactCallback<Contact>() {
                     @Override
                     public void onSuccess(Contact contact) {
                        if (contact != null) {
                           holder.btnAdd.setIconResource(R.drawable.ic_check);
                        }
                     }

                     @Override
                     public void onError(Throwable t) {}
                  }
               );
            }
         }
      );
   }

   @Override
   public int getItemCount() {
      return userList.size();
   }

   public void setUserList(List<User> userList) {
      this.userList = userList;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView txtName;
      private final MaterialButton btnAdd;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         imageView = itemView.findViewById(R.id.imgContItem);
         txtName = itemView.findViewById(R.id.txtContItemName);
         btnAdd = itemView.findViewById(R.id.btnContItemAdd);
      }
   }
}
