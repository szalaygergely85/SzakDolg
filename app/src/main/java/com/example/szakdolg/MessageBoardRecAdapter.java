package com.example.szakdolg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class MessageBoardRecAdapter extends RecyclerView.Adapter<MessageBoardRecAdapter.ViewHolder> {
    private final FirebaseConnect firebaseConnect;
    private final SQLConnect sqlConnect;
    private ArrayList<MessageB> messageB = new ArrayList<>();
    private final Context mContext;
    private static final String TAG = "MessageBoardRecAdapter";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    public MessageBoardRecAdapter(Context mContext) {
        this.mContext = mContext;
        firebaseConnect = FirebaseConnect.getInstance("firebase");
        sqlConnect = SQLConnect.getInstance("sql", firebaseConnect.getUserId());
    }

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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_board_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (messageB.get(position).isRead() == 0) {
            holder.txtMessage.setTypeface(null, Typeface.BOLD);
            holder.txtName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.txtMessage.setTypeface(null, Typeface.NORMAL);
            holder.txtName.setTypeface(null, Typeface.NORMAL);
        }
        holder.txtName.setText(messageB.get(position).getContactUserName());
        holder.txtMessage.setText(messageB.get(position).getText());
        setImageView(messageB.get(position).getContactId(), mContext, holder.image);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, messageB.get(position).getContactId());
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("uID", messageB.get(position).getContactId());
                mContext.startActivity(intent);

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
