package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.activity.ProfileActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserCoordinatorService;

import java.util.ArrayList;
import java.util.List;


public class SelectContactsAdapter extends RecyclerView.Adapter<SelectContactsAdapter.ViewHolder>{

    private final Context context;

    private final User currentUser;

    private  UserCoordinatorService userCoordinatorService;

    private List<Contact> contacts = new ArrayList<>();

    private List<Long> selectedUsers;

    public SelectContactsAdapter(Context context, User currentUser, List<Long> selectedUsers) {
        this.context = context;
        this.currentUser = currentUser;
        this.userCoordinatorService = new UserCoordinatorService(context);
        this.selectedUsers = selectedUsers;
    }

    @NonNull
    @Override
    public SelectContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_select_contact, parent, false);
        SelectContactsAdapter.ViewHolder holder = new SelectContactsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectContactsAdapter.ViewHolder holder, int position) {
    Contact contact = contacts.get(holder.getAdapterPosition());
//TODO will need to do to download the users....
    User user = userCoordinatorService.getUserByUserId(contact.getContactUserId(), currentUser);

String displayName = user.getDisplayName();
if(displayName!=null) {
    holder.txtName.setText(displayName);
}

        holder.linearLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedUsers.contains(user)){
                            selectedUsers.remove(user.getUserId());
                            holder.checkImageView.setVisibility(View.INVISIBLE);

                        }else {
                            selectedUsers.add(user.getUserId());
                            holder.checkImageView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView checkImageView;
        private final ImageView profileImageView;
        private final TextView txtName;
        private final LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.contactSelectName);
            linearLayout = itemView.findViewById(R.id.contactSelect_ll);
            checkImageView = itemView.findViewById(R.id.imageView2);
            profileImageView = itemView.findViewById(R.id.contactSelectImage);
        }
    }
}
