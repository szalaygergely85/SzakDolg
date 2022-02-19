package com.example.szakdolg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private ArrayList<Chat> chats = new ArrayList<>();
    private FirebaseConnect firebaseConnect = new FirebaseConnect();


    public ChatAdapter() {


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (chats.get(position).getSender().equals(firebaseConnect.getUserId())){
            holder.txtTextFrMe.setText(chats.get(position).getMessage());
            holder.txtText.setVisibility(View.GONE);
        }else{
            holder.txtText.setText(chats.get(position).getMessage());
            holder.txtTextFrMe.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        //declare here fields
        private TextView txtText;
        private TextView txtTextFrMe;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtText = itemView.findViewById(R.id.chatText);
            txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
        }
    }


}
