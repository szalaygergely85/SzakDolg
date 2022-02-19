package com.example.szakdolg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Contact> contact = new ArrayList<>();

    FirebaseConnect firebaseConnect = new FirebaseConnect();

    public SearchContactAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_search_item, parent, false);
        SearchContactAdapter.ViewHolder holder = new SearchContactAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(contact.get(position).getName());
        holder.txtEmail.setText(contact.get(position).getEmail());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseConnect.addContactFB(contact.get(position));
                //TODO add Database this
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
        private ImageButton btnAdd;
        private FirebaseFirestore db;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtContItemName);
            txtEmail = itemView.findViewById(R.id.txtContItemEmail);
            btnAdd = itemView.findViewById(R.id.btnContItemAdd);
        }
    }
}

