package com.example.szakdolg.messageboard.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.szakdolg.R;
import com.example.szakdolg.model.image.ImageCoordinator;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserService;

public class MessageBoardAdapterHelper {

   private User currentUser;
   private Context context;

   private UserService userService;

   public MessageBoardAdapterHelper(User currentUser, Context context) {
      this.currentUser = currentUser;
      this.context = context;
      this.userService = new UserService(context);
   }

   public void setImageView(Long userId, Context context, ImageView image) {
      User user = userService.getUserByUserId(userId, currentUser);

      ImageCoordinator imageCoordinator = new ImageCoordinator(
         context,
         currentUser
      );

      Uri uri = imageCoordinator.getImage(user);
      if (uri != null) {
         Glide
            .with(context)
            .load(uri)
            .placeholder(R.drawable.ic_blank_profile) // Default image while loading
            .error(R.drawable.ic_blank_profile) // Default image on error
            .into(image);
      }
   }
}
