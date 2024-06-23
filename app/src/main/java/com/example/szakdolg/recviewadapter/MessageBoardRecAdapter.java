package com.example.szakdolg.recviewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserApiHelper;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardRecAdapter extends RecyclerView.Adapter<MessageBoardRecAdapter.ViewHolder> {

    private ArrayList<MessageBoard> messageB = new ArrayList<>();
    private final Context mContext;

    private User loggedUser;


    private static final String TAG = "MessageBoardRecAdapter";


    ConversationApiHelper conversationApiHelper = new ConversationApiHelper();
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MessageBoard messageBoard = messageB.get(position);
        MessageEntry messageEntry = messageBoard.getMessage();

        if(messageEntry.getContent()!=null) {
            User participant = UserUtil.removeCurrentUserFromList(messageBoard.getParticipants(), loggedUser.getUserId());


            if (messageEntry.isRead() || loggedUser.getUserId().equals(messageEntry.getSenderId())) {
                holder.txtMessage.setTypeface(null, Typeface.NORMAL);
                holder.txtName.setTypeface(null, Typeface.NORMAL);

            } else {
                holder.txtMessage.setTypeface(null, Typeface.BOLD);
                holder.txtName.setTypeface(null, Typeface.BOLD);

            }
            String decryptedContentString = null;
            try {
                decryptedContentString = EncryptionHelper.decrypt(messageEntry.getContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            holder.txtName.setText(participant.getDisplayName());
            holder.txtMessage.setText(decryptedContentString);

            // setImageView(messageB.get(position).getContactId(), mContext, holder.image);
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    conversationApiHelper.openConversation(messageBoard.getConversationId(), mContext, loggedUser);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageB.size();
    }

    public void setMessageB(ArrayList<MessageBoard> messageB) {
        this.messageB = messageB;
        notifyDataSetChanged();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
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
