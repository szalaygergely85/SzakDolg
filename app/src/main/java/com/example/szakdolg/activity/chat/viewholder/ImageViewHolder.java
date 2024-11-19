package com.example.szakdolg.activity.chat.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.models.file.api.FileApiHelper;
import com.example.szakdolg.util.FileUtil;

public class ImageViewHolder extends RecyclerView.ViewHolder {

   private FileApiHelper fileApiHelper = new FileApiHelper();
   ImageView imageView;

   public ImageViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.imageView);
   }

   public void bind(String imageUrl, Context context, Runnable runnable) {
      Uri uri = FileUtil.getUri(imageUrl, imageView.getContext());
      if (uri != null) {
         imageView.setImageURI(uri);
      } else {
         fileApiHelper.getFile(imageUrl, context, runnable);
      }
   }
}
