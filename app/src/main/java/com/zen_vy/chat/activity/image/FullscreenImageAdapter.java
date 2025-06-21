package com.zen_vy.chat.activity.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.zen_vy.chat.R;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;

public class FullscreenImageAdapter
   extends RecyclerView.Adapter<FullscreenImageAdapter.ImageViewHolder> {

   private final List<String> imageUrls;
   private final Context context;

   private final User currentUser;

   public FullscreenImageAdapter(
      Context context,
      List<String> imageUrls,
      User currentUser
   ) {
      this.context = context;
      this.imageUrls = imageUrls;
      this.currentUser = currentUser;
   }

   static class ImageViewHolder extends RecyclerView.ViewHolder {

      ZoomableImageView imageView;

      ImageViewHolder(View itemView) {
         super(itemView);
         imageView = itemView.findViewById(R.id.fullImageView);
      }
   }

   @NonNull
   @Override
   public ImageViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.item_fullscreen_image, parent, false);
      return new ImageViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
      String imageUrl = ImageUtil.buildImageUrl(imageUrls.get(position));

      GlideUrl glideUrl = ImageUtil.getGlideUrlWithTokenHeader(
         imageUrl,
         currentUser.getToken()
      );

      Glide
         .with(context)
         .load(glideUrl)
         .diskCacheStrategy(DiskCacheStrategy.ALL)
         .placeholder(R.drawable.bg_search) // your placeholder drawable
         .error(R.drawable.bg_search) // your error drawable
         .into(holder.imageView);
   }

   @Override
   public int getItemCount() {
      return imageUrls.size();
   }
}
