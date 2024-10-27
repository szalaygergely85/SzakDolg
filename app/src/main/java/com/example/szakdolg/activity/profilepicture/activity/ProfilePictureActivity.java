package com.example.szakdolg.activity.profilepicture.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.szakdolg.activity.profilepicture.helper.ProfilePictureActivityHelper;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.model.user.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.szakdolg.R;

import java.io.IOException;

public class ProfilePictureActivity extends AppCompatActivity {
    private Button continueButton;
    private Button skipButton;
    private ImageView _editIcon;
    private ShapeableImageView _profileImageView;
    private ProfilePictureActivityHelper _profilePictureActivityHelper;
    private User currentUser;

    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

       /* currentUser =
                (User) this.getIntent()
                        .getSerializableExtra(SharedPreferencesConstants.CURRENT_USER);*/
        currentUser = new User(123L, "gege", "aaa@aaa.hu");
        currentUser.setUserId(1234L);


        _initView();

        _profilePictureActivityHelper = new ProfilePictureActivityHelper(this, currentUser);
        _setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void _initView() {
        _editIcon = findViewById(R.id.editIcon);
        _profileImageView = findViewById(R.id.profileImageView);
        skipButton = findViewById(R.id.skipButton);
        continueButton = findViewById(R.id.continueButton);
    }

    private void _setOnClickListeners() {
        _profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        _editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle skip button click
                // For example, you could skip the profile picture step
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null) {
                    _profilePictureActivityHelper.addImage(imageUri);
                }
            }
        });
    }


    public void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Set the type to images
        pickImageLauncher.launch(intent); // Launch the image picker using the ActivityResultLauncher
    }
    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        _profilePictureActivityHelper.setImageViewer(this, imageUri, _profileImageView);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

}