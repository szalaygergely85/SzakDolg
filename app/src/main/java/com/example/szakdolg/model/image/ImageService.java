package com.example.szakdolg.model.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.szakdolg.db.util.ImageDatabaseUtil;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.UUIDUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageService {
    private Context context;

    private User user;

    public ImageService(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    public void addImage(ImageEntity imageEntity) {
        Uri imageUri = Uri.parse(imageEntity.getImageUri());

        Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        int maxSize = 4000;
                        if (imageEntity.getTags() != null && imageEntity.getTags().equals(ImageConstans.TAG_PROFILE)){
                            maxSize = 800;
                        }
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        // Calculate the new dimensions while maintaining the aspect ratio
                        float aspectRatio = (float) width / height;

                        int newWidth, newHeight;

                        if (width > height) {
                            newWidth = maxSize;
                            newHeight = Math.round(maxSize / aspectRatio);
                        } else {
                            newHeight = maxSize;
                            newWidth = Math.round(maxSize * aspectRatio);
                        }

                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                        // Save locally
                        saveImageLocally(resizedBitmap, imageEntity.getFileName());

                        imageEntity.setHeight(newHeight);
                        imageEntity.setWidth(newWidth);
                        imageEntity.setSize(resizedBitmap.getByteCount());

                        // Add to the database

                        ImageDatabaseUtil imageDatabaseUtil = new ImageDatabaseUtil(context, user);
                        imageDatabaseUtil.insertImageEntity(imageEntity);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    public void createAndAddImage(Uri imageUri, Long userId, String name, String mimeType, String tags) {
        ImageEntity imageEntity = new ImageEntity(imageUri.toString(), userId, mimeType, System.currentTimeMillis(), ImageConstans.STATUS_PENDING, ImageConstans.TAG_PROFILE, UUIDUtil.UUIDGenerator());

        if (tags != null && tags.equals(ImageConstans.TAG_PROFILE)){
            imageEntity.setFileName(name + "_profile_image");
        }else{
            imageEntity.setFileName(name);
        }
        addImage(imageEntity);
    }


    private void saveImageLocally(Bitmap bitmap, String name) {

        File path = new File(context.getFilesDir() + "/Pictures/");
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path + "/" + name + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
