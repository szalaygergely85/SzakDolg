package com.example.szakdolg.activity.profilepicture.helper;


import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.model.image.constans.ImageConstans;
import com.example.szakdolg.model.image.service.ImageService;
import com.example.szakdolg.model.user.model.User;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfilePictureActivityHelper {

    private Context context;
    private User user;

    public ProfilePictureActivityHelper(Context context, User user) {
        this.context = context;
        this.user = user;
    }




    public void addImage(Uri imageUri, ShapeableImageView imageView) {
        String mimeType = getMimeType(imageUri);
        if (mimeType != null && (mimeType.startsWith("image/"))) {

            ImageService imageService = new ImageService(context, user);
            imageService.createAndAddImage(imageUri, user.getUserId(), mimeType, ImageConstans.TAG_PROFILE);

            imageView.setImageURI(imageUri);
        } else {
            Log.e(AppConstants.LOG_TAG, "Invalid image. Format not supported.");
        }
    }
    public String getMimeType(Uri uri) {
        String mimeType = null;
        ContentResolver contentResolver = context.getContentResolver();
        mimeType = ((ContentResolver) contentResolver).getType(uri);
        return mimeType;
    }

}
