package com.example.szakdolg.activity.profilepicture.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

public class ProfilePictureActivity extends AppCompatActivity {

    private ImageView _editIcon;
    private ShapeableImageView _profileImageView;
    private ProfilePictureActivityHelper _profilePictureActivityHelper;

    private User currentUser;

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
        _profileImageView = findViewById(R.id.profileImageView);
        _editIcon = findViewById(R.id.editIcon);
        _profileImageView = findViewById(R.id.profileImageView);
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
                    Uri imageUri = result.getData().getData();

                    _profilePictureActivityHelper.addImage(imageUri, _profileImageView);
                }
            });

}