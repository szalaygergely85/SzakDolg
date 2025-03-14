package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.ProfileActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter
   extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

   private static final String TAG = "ContactsAdapter";

   private User user;
   private final Context mContext;
   private List<User> contact = new ArrayList<>();

   public ContactsAdapter(Context mContext, User user) {
      this.mContext = mContext;
      this.user = user;
   }


   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.contact_item, parent, false);
      ViewHolder holder = new ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User contactUser = contact.get(holder.getAdapterPosition());

      holder.txtName.setText(contactUser.getDisplayName());
      holder.txtEmail.setText(contactUser.getEmail());
      holder.relativeLayout.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(mContext, ProfileActivity.class);
               intent.putExtra(
                  SharedPreferencesConstants.OTHER_USER,
                  contactUser
               );
               intent.putExtra(SharedPreferencesConstants.CURRENT_USER, user);
               mContext.startActivity(intent);
               Toast
                  .makeText(
                     view.getContext(),
                     contactUser.toString(),
                     Toast.LENGTH_SHORT
                  )
                  .show();
            }
         }
      );
   }

   @Override
   public int getItemCount() {
      return contact.size();
   }

   public void setContact(List<User> contact) {
      this.contact = contact;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView txtName;
      private final TextView txtEmail;
      private final TextView txtPhone;
      private final RelativeLayout relativeLayout;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         txtName = itemView.findViewById(R.id.txtContName);
         txtEmail = itemView.findViewById(R.id.txtContEmail);
         txtPhone = itemView.findViewById(R.id.txtContPhone);
         relativeLayout = itemView.findViewById(R.id.relLayContact);
         imageView = itemView.findViewById(R.id.imgCont);
      }
   }
}
