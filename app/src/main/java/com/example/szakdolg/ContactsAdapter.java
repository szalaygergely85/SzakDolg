package com.example.szakdolg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static final String TAG = "ContactsAdapter";
    private final Context mContext;
    private ArrayList<Contact> contact = new ArrayList<>();
    private final FirebaseConnect firebaseConnect;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
        firebaseConnect = FirebaseConnect.getInstance("firebase");
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
                                    // Handle any errors
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(contact.get(position).getName());
        holder.txtEmail.setText(contact.get(position).getEmail());
        setImageView(contact.get(position).getID(), mContext, holder.imageView);
        holder.txtPhone.setText(contact.get(position).getPhone());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("uID", contact.get(position).getID());
                mContext.startActivity(intent);
                Toast.makeText(view.getContext(), contact.get(position).getEmail(), Toast.LENGTH_SHORT).show();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView txtName;
        private final TextView txtEmail;
        private final TextView txtPhone;
        private final RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtContName);
            txtEmail = itemView.findViewById(R.id.txtContEmail);
            txtPhone = itemView.findViewById(R.id.txtContPhone);
            relativeLayout = itemView.findViewById(R.id.relLayContact);
            imageView = itemView.findViewById(R.id.imgCont);
        }
    }
}
