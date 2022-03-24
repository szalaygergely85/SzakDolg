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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Chat> chats = new ArrayList<>();
    private FirebaseConnect firebaseConnect;
    private String userID;
    long time;
    public ChatAdapter(Context mContext) {
        this.mContext = mContext;

        firebaseConnect = new FirebaseConnect(mContext);
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
            // Log.d("test", ""+time);
        }catch (Exception e){
            // Log.d("Error", e.toString());
        }

        Date date = new Date(time);

        Format format = new SimpleDateFormat("HH:mm");
        String timeForm = format.format(date);
        Log.d("Chat", chats.toString());
        if (chats.get(position).isFromMe()>0){
            holder.txtTextFrMe.setText(chats.get(position).getMessage());
            holder.txtTimeOut.setText(timeForm);
            holder.relIn.setVisibility(View.GONE);
        }else{
            holder.txtText.setText(chats.get(position).getMessage());
            holder.txtTimeIn.setText(timeForm);
            holder.relOut.setVisibility(View.GONE);
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
        private TextView txtTimeIn;
        private TextView txtTimeOut;
        private TextView txtTextFrMe;
        private RelativeLayout relIn;
        private RelativeLayout relOut;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtText = itemView.findViewById(R.id.chatText);
            txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
            txtTimeIn =itemView.findViewById(R.id.chatTextTimeIn);
            txtTimeOut=itemView.findViewById(R.id.chatTextTimeOut);
            relIn = itemView.findViewById(R.id.chatRelIn);
            relOut = itemView.findViewById(R.id.chatRelOut);
        }
    }


}
