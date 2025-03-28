package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.ContactsDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.models.user.entity.User;

import java.util.ArrayList;
import java.util.List;


public class SelectContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context context;

    private final User currentUser;


    private List<Object> usersList = new ArrayList<>();

    private List<Long> selectedUsers = new ArrayList<>();

    public SelectContactsAdapter(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
        selectedUsers.add(currentUser.getUserId());
    }


    @Override
    public int getItemViewType(int position) {
        if (usersList.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_contact_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_contact_user, parent, false);
            return new ContactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerText.setText((String) usersList.get(position));
        } else {
            User user = (User) usersList.get(position);
            String displayName = user.getDisplayName();
            if (displayName != null) {
                ((ContactViewHolder)holder).txtName.setText(displayName);
            }

            ((ContactViewHolder)holder).linearLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectedUsers.contains(user.getUserId())) {
                                selectedUsers.remove(user.getUserId());
                                ((ContactViewHolder)holder).checkImageView.setVisibility(View.INVISIBLE);

                            } else {
                                selectedUsers.add(user.getUserId());
                                ((ContactViewHolder)holder).checkImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
            );
        }

    }

    public List<Long> getSelectedUsers(){
        return selectedUsers;
    }

    public void setUsers(List<Object> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
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
