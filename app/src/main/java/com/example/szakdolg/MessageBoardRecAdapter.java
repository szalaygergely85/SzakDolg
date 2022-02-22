package com.example.szakdolg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageBoardRecAdapter extends RecyclerView.Adapter<MessageBoardRecAdapter.ViewHolder> {
    private FirebaseConnect firebaseConnect = new FirebaseConnect();
    private SQLConnect sqlConnect = new SQLConnect();
    private ArrayList<MessageB> messageB = new ArrayList<>();

    public MessageBoardRecAdapter(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_board_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (messageB.get(position).getFrom().toString().equals(firebaseConnect.getUserId())){
            holder.txtName.setText(sqlConnect.getNameFrContact(messageB.get(position).getTo().toString()));
        }else {
            holder.txtName.setText(sqlConnect.getNameFrContact(messageB.get(position).getFrom().toString()));
        }
        holder.txtMessage.setText(messageB.get(position).getMessage());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                if (messageB.get(position).getFrom().toString().equals(firebaseConnect.getUserId())){
                    intent.putExtra("uID", messageB.get(position).getTo());
                }else {
                    intent.putExtra("uID", messageB.get(position).getFrom());
                }
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
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
