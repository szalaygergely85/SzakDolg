package com.example.szakdolg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private static final int PHOTO_PICKER_REQUEST_CODE = 101;
    private static final String TAG = "ProfileActivity";

    private ImageView imageView;
    private TextView name;
    private TextView email;
    private Button changePW;
    private Button singOut;
    private Button deleteAccount;
    private Button deleteContact;
    private Button sendMessage;
    private FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private String uID;

    public void setImageView(String uID, Context context) {

        Log.d(TAG, "getPicURl: " + uID);
        Uri picUri = FileHandling.getUri(uID, context);
        if (picUri == null) {
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
                                                        FileHandling.saveImageFile(uID, resource, ProfileActivity.this);
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
                                            .into(imageView);
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
            imageView.setImageURI(FileHandling.getUri(uID, context));
        }


    }

    private void initView() {
        imageView = findViewById(R.id.profPic);
        name = findViewById(R.id.profTxtName);
        email = findViewById(R.id.profTxtEmail);
        changePW = findViewById(R.id.profBtnChangePass);
        singOut = findViewById(R.id.profBtnSignOut);
        deleteAccount = findViewById(R.id.profBtnDelete);
        deleteContact = findViewById(R.id.profBtnDeleteContact);
        sendMessage = findViewById(R.id.profBtnSendMsg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uID = (String) this.getIntent().getSerializableExtra("uID");

        initView();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.profBoardToolbar);
        setSupportActionBar(mToolbar);
        //toolbar settings


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Profile");


        if (uID != null) {
            changePW.setVisibility(View.GONE);
            singOut.setVisibility(View.GONE);
            deleteAccount.setVisibility(View.GONE);
        } else {
            uID = firebaseConnect.getUserId();
            deleteContact.setVisibility(View.GONE);
            sendMessage.setVisibility(View.GONE);
        }

        setImageView(uID, this);

        firebaseConnect.db.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, document.get("name").toString());
                        name.setText(document.get("name").toString());
                        email.setText(document.get("email").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FireBase", e.toString());
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: choosed smthing");
        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    Log.d(TAG, "onActivityResult: " + selectedImage);

                    firebaseConnect.uploadPic(selectedImage);


                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImage)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    try {
                                        FileHandling.saveImageFile(uID, resource, ProfileActivity.this);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImage)
                            .into(imageView);
                }
                break;
            default:
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseConnect.logoutUser();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseConnect.deleteAccount(firebaseConnect.getUserId(), ProfileActivity.this);
                finish();
            }
        });
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseConnect.deleteAccount(uID, ProfileActivity.this);
                finish();
            }
        });
        changePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putExtra("uID", uID);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}