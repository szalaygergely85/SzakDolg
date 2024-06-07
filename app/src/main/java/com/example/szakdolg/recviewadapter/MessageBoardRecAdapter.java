package com.example.szakdolg.recviewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.MessageBoard;

import com.example.szakdolg.R;
import com.example.szakdolg.activity.ChatActivity;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.User;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardRecAdapter extends RecyclerView.Adapter<MessageBoardRecAdapter.ViewHolder> {

    private ArrayList<MessageBoard> messageB = new ArrayList<>();
    private final Context mContext;

    private User user;
    private static final String TAG = "MessageBoardRecAdapter";


    public MessageBoardRecAdapter(Context mContext) {
        this.mContext = mContext;
        ;
    }

    /*
    public void setImageView(String uID, Context context, ImageView image) {
        Uri picUri = null;
        Log.d(TAG, "getPicURl: " + uID);
        try {
            picUri = FileHandling.getUri(uID, context);
        } catch (Exception e) {
            Log.d(TAG, "setImageView: " + e);
        }
        if (picUri == null) {
            Log.d(TAG, "setImageView: couldn't find the pic");
            try {
                storageRef.child(uID + ".jpg").getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                    @Override
                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                        if (task.isSuccessful()) {

                            storageRef.child(uID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "getPicURl: " + uri);
                                    Glide.with(context)
                                            .asBitmap()
                                            .load(uri)
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    try {
                                                        FileHandling.saveImageFile(uID, resource, context);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });

                                    Glide.with(context)
                                            .asBitmap()
                                            .load(uri)
                                            .into(image);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    image.setImageResource(R.drawable.ic_blank_profile);
                                    Log.d(TAG, "onFailure: " + exception);
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                Log.d(TAG, "setImageView: " + e);
            }

        } else {
            Log.d(TAG, "setImageView: " + picUri);
            image.setImageURI(FileHandling.getUri(uID, context));
        }
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_board_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public static User findUserById(List<User> users, Long id) {
        for (User user : users) {
            if (user.getUserId() == id) {
                return user;
            }
        }
        return null; // or throw an exception, or return an Optional<User>
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MessageBoard messageBoard = messageB.get(position);
        MessageEntry messageEntry = messageBoard.getMessage();

        User sender = findUserById(messageBoard.getParticipants(), messageEntry.getSenderId());


        //if (messageB.get(position).isRead() == 0) {
            holder.txtMessage.setTypeface(null, Typeface.BOLD);
            holder.txtName.setTypeface(null, Typeface.BOLD);
       // } else {
        //    holder.txtMessage.setTypeface(null, Typeface.NORMAL);
        //    holder.txtName.setTypeface(null, Typeface.NORMAL);
       // }


        holder.txtName.setText(sender.getFirstName());
        holder.txtMessage.setText(messageEntry.getContent());
        // setImageView(messageB.get(position).getContactId(), mContext, holder.image);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, messageB.get(position).getContactId());
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("conversationId", messageBoard.getConversationId());
                intent.putExtra("user", user);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageB.size();
    }

    public void setMessageB(ArrayList<MessageBoard> messageB) {
        this.messageB = messageB;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtMessage;
        private final RelativeLayout parent;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.mesBrdImage);
            txtName = itemView.findViewById(R.id.mesBrdName);
            txtMessage = itemView.findViewById(R.id.mesBrdMessage);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
