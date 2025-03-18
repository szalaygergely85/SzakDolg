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
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter
   extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

   private static final String TAG = "ContactsAdapter";

   private User currentUser;
   private final Context context;
   private List<Contact> contacts = new ArrayList<>();

   private UserService userService;


   public ContactsAdapter(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;

      this.userService = new UserService(context);
   }


   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.item_contacts, parent, false);
      ViewHolder holder = new ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      Contact contact = contacts.get(holder.getAdapterPosition());
      userService.getUserByUserId(contact.getContactUserId(), currentUser, new UserService.UserCallback<User>() {
                 @Override
                 public void onSuccess(User user) {
                    holder.txtName.setText(user.getDisplayName());
                    holder.relativeLayout.setOnClickListener(
                            new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                  Intent intent = new Intent(context, ProfileActivity.class);
                                  intent.putExtra(
                                          SharedPreferencesConstants.OTHER_USER,
                                          user
                                  );
                                  intent.putExtra(SharedPreferencesConstants.CURRENT_USER, currentUser);
                                  context.startActivity(intent);
                                  Toast
                                          .makeText(
                                                  view.getContext(),
                                                  user.toString(),
                                                  Toast.LENGTH_SHORT
                                          )
                                          .show();
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
      return contacts.size();
   }

   public void setContact(List<Contact> contacts) {
      this.contacts = contacts;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView txtName;
      private final RelativeLayout relativeLayout;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         txtName = itemView.findViewById(R.id.contactName);
         relativeLayout = itemView.findViewById(R.id.relLayContact);
         imageView = itemView.findViewById(R.id.contactImage);
      }
   }
}
