package com.example.szakdolg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder> {
    private static final String TAG = "SearchContactAdapter";
    private Context mContext;
    private ArrayList<Contact> contact = new ArrayList<>();
    private FirebaseConnect firebaseConnect;
    private SQLConnect sqlConnect;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    public void setImageView(String uID, Context context, ImageView image) {
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
    }


    public SearchContactAdapter(Context mContext) {
        this.mContext = mContext;
        firebaseConnect = FirebaseConnect.getInstance("firebase");
        sqlConnect = SQLConnect.getInstance("sql", firebaseConnect.getUserId());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_search_item, parent, false);
        SearchContactAdapter.ViewHolder holder = new SearchContactAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(contact.get(position).getName());
        holder.txtEmail.setText(contact.get(position).getEmail());
        setImageView(contact.get(position).getID(), mContext, holder.imageView);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!sqlConnect.isInContracts(contact.get(position).getID())) {
                    firebaseConnect.addAUser(contact.get(position).getID());
                    Toast.makeText(mContext, contact.get(position).getName() + " added to your contacts", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(mContext, "You already have " + contact.get(position).getName() + " in your contacts", Toast.LENGTH_SHORT).show();
                }
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
        private ImageView imageView;
        private TextView txtName;
        private TextView txtEmail;
        private ImageButton btnAdd;
        private FirebaseFirestore db;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgContItem);
            txtName = itemView.findViewById(R.id.txtContItemName);
            txtEmail = itemView.findViewById(R.id.txtContItemEmail);
            btnAdd = itemView.findViewById(R.id.btnContItemAdd);
        }
    }
}

