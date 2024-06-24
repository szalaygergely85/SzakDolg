package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

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
    private User userContact;
    private User userLoggedIn;
    private ConversationApiHelper conversationApiHelper= new ConversationApiHelper();


/*
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
            imageView.setImageURI(FileHandling.getUri(uID, context));
        }
    }
*/

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

        userLoggedIn = (User) this.getIntent().getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);

        userContact = (User) this.getIntent().getSerializableExtra(SharedPreferencesConstans.OTHER_USER);

        initView();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.profBoardToolbar);
        setSupportActionBar(mToolbar);
        //toolbar settings

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Profile");

        if (userContact != null) {
            changePW.setVisibility(View.GONE);
            singOut.setVisibility(View.GONE);
            deleteAccount.setVisibility(View.GONE);

            name.setText(userContact.getFullName() + " " + userContact.getDisplayName());
            email.setText(userContact.getEmail());
        } else {

            deleteContact.setVisibility(View.GONE);
            sendMessage.setVisibility(View.GONE);

            name.setText(userLoggedIn.getFullName() + " " + userLoggedIn.getDisplayName());
            email.setText(userLoggedIn.getEmail());
        }
        // setImageView(uID, this);



    }


    @Override
    protected void onStart() {
        super.onStart();
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferencesUtil.deleteStringPreference(ProfileActivity.this, SharedPreferencesConstans.USERTOKEN);

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
                Log.d(TAG, "onClick: clicked on delete account");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                startActivity(intent);
                finish();
            }
        });
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO create delete contact


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
                List<User> participants = new ArrayList<>();
                participants.add(userContact);
                participants.add(userLoggedIn);
                conversationApiHelper.openConversation(ProfileActivity.this, null, participants, userLoggedIn);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}