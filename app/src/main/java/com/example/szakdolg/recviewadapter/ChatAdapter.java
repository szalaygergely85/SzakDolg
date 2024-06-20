package com.example.szakdolg.recviewadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.Chat;
import com.example.szakdolg.R;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";
    private final Context mContext;
    private ArrayList<Chat> chats = new ArrayList<>();
    private List<MessageEntry> messageEntries = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private final User userLoggedIn;

    long time;

    public ChatAdapter(Context mContext, User user) {
        this.mContext = mContext;
        this.userLoggedIn = user;
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
        MessageEntry messageEntry = messageEntries.get(holder.getAdapterPosition());

            time = (messageEntry.getTimestamp());

        Date date = new Date(time);

            Format format = new SimpleDateFormat("HH:mm");
            String timeForm = format.format(date);

        String decryptedContentString = null;
        try {
       //     decryptedContentString = EncryptionHelper.decrypt(messageEntry.getContent(), KeyStoreUtil.getPrivateKey(userLoggedIn.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

         //   holder.txtTextFrMe.setText(decryptedContentString);
            holder.txtTimeOut.setText(timeForm);

            if (messageEntry.getSenderId() == userLoggedIn.getUserId()) {
                holder.relIn.setVisibility(View.GONE);
                holder.relOut.setVisibility(View.VISIBLE);
            } else {
                holder.relOut.setVisibility(View.GONE);
                holder.relIn.setVisibility(View.VISIBLE);
                holder.txtText.setText(messageEntry.getContent());
            }


    }

    @Override
    public int getItemCount() {
        return messageEntries.size();
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();

    }

    public void setMessageEntries(List<MessageEntry> messageEntries) {
        this.messageEntries = messageEntries;
        notifyDataSetChanged();
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
