package com.example.szakdolg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";
    private final Context mContext;
    private ArrayList<Chat> chats = new ArrayList<>();
    private final FirebaseConnect firebaseConnect;
    private final String userID;
    long time;

    public ChatAdapter(Context mContext) {
        this.mContext = mContext;
        firebaseConnect = FirebaseConnect.getInstance("firebase");
        userID = firebaseConnect.getUserId();
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
        try {
            time = Long.parseLong(chats.get(position).getId());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        Date date = new Date(time);
        if (!chats.get(position).getMessage().isEmpty()) {
            Format format = new SimpleDateFormat("HH:mm");
            String timeForm = format.format(date);
            Log.d(TAG, "" + chats.get(position).isFromMe());
            if (chats.get(position).isFromMe() > 0) {
                holder.txtTextFrMe.setText(chats.get(position).getMessage());
                holder.txtTimeOut.setText(timeForm);
                holder.relIn.setVisibility(View.GONE);
                holder.relOut.setVisibility(View.VISIBLE);
            } else {
                holder.txtText.setText(chats.get(position).getMessage());
                holder.txtTimeIn.setText(timeForm);
                holder.relOut.setVisibility(View.GONE);
                holder.relIn.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "onBindViewHolder: Message is Empty");
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        //declare here fields
        private final TextView txtText;
        private final TextView txtTimeIn;
        private final TextView txtTimeOut;
        private final TextView txtTextFrMe;
        private final RelativeLayout relIn;
        private final RelativeLayout relOut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtText = itemView.findViewById(R.id.chatText);
            txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
            txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
            txtTimeOut = itemView.findViewById(R.id.chatTextTimeOut);
            relIn = itemView.findViewById(R.id.chatRelIn);
            relOut = itemView.findViewById(R.id.chatRelOut);
        }
    }
}
