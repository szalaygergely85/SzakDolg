package com.example.szakdolg;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Contact> contact = new ArrayList<>();
    FirebaseConnect firebaseConnect = new FirebaseConnect();

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(contact.get(position).getName());
        holder.txtEmail.setText(contact.get(position).getEmail());
        holder.txtPhone.setText(contact.get(position).getPhone());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("uID", contact.get(position).getID());
                 mContext.startActivity(intent);
                Toast.makeText(view.getContext(), contact.get(position).getEmail(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public void setContact(ArrayList<Contact> contact) {
        this.contact = contact;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private TextView txtEmail;
        private TextView txtPhone;
        private RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtContName);
            txtEmail = itemView.findViewById(R.id.txtContEmail);
            txtPhone = itemView.findViewById(R.id.txtContPhone);
            relativeLayout = itemView.findViewById(R.id.relLayContact);
        }
    }
}
