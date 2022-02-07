package com.example.szakdolg;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageBoardRecAdapter extends RecyclerView.Adapter<MessageBoardRecAdapter.ViewHolder> {

    private ArrayList<MessageB> messageB = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_board_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtName.setText(messageB.get(position).getName());
        holder.txtMessage.setText(messageB.get(position).getMessage());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Log.d("Clicked", messageB.get(position).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageB.size();
    }

    public void setMessageB(ArrayList<MessageB> messageB) {
        this.messageB = messageB;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //ide jonnek majd az elemek az xmlbol
        private TextView txtName;
        private TextView txtMessage;
        private RelativeLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.mesBrdName);
            txtMessage = itemView.findViewById(R.id.mesBrdMessage);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
