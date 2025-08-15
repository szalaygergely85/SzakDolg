package com.zen_vy.chat.activity.image;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.image.util.ImageUtil;
import java.util.ArrayList;
import timber.log.Timber;

public class FullscreenImageActivity extends BaseActivity {

   private ViewPager2 viewPager;
   private ImageButton btnClose, btnDownload, btnLeft, btnRight;
   private ArrayList<String> imageUrls;
   private String currentImageUrl;
   private FullscreenImageAdapter adapter;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_fullscreen_image);

      viewPager = findViewById(R.id.viewPager);
      btnClose = findViewById(R.id.btnClose);
      btnDownload = findViewById(R.id.btnDownload);
      btnLeft = findViewById(R.id.btnLeft);
      btnRight = findViewById(R.id.btnRight);

      // Get imageUris list passed via intent extras
      imageUrls = getIntent().getStringArrayListExtra(IntentConstants.IMAGES);

      currentImageUrl = getIntent().getStringExtra(IntentConstants.IMAGE);

      if (imageUrls == null || imageUrls.isEmpty()) {
         Timber.e("No images to display");
         finish();
         return;
      }

      adapter =
      new FullscreenImageAdapter(
         this,
         imageUrls,
         currentUser,
         btnLeft,
         btnRight
      );

      viewPager.setAdapter(adapter);

      viewPager.setCurrentItem(imageUrls.indexOf(currentImageUrl));

      viewPager.registerOnPageChangeCallback(
         new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
               super.onPageSelected(position);

               int itemCount = adapter.getItemCount();

               if (itemCount < 2) {
                  btnLeft.setVisibility(View.GONE);
                  btnRight.setVisibility(View.GONE);
               } else if (position == 0) {
                  btnLeft.setVisibility(View.GONE);
                  btnRight.setVisibility(View.VISIBLE);
               } else if (position == itemCount - 1) {
                  btnLeft.setVisibility(View.VISIBLE);
                  btnRight.setVisibility(View.GONE);
               } else {
                  btnLeft.setVisibility(View.VISIBLE);
                  btnRight.setVisibility(View.VISIBLE);
               }
            }
         }
      );

      // Button listeners
      btnClose.setOnClickListener(v -> finish());

      btnLeft.setOnClickListener(v -> {
         int current = viewPager.getCurrentItem();
         if (current > 0) {
            viewPager.setCurrentItem(current - 1);
         }
      });

      btnRight.setOnClickListener(v -> {
         int current = viewPager.getCurrentItem();
         if (current < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(current + 1);
         }
      });

      btnDownload.setOnClickListener(v -> {
         String imageUrl = ImageUtil.buildImageUrl(
            imageUrls.get(viewPager.getCurrentItem())
         );
         String fileName = "image_" + System.currentTimeMillis() + ".jpg";

         DownloadManager.Request request = new DownloadManager.Request(
            Uri.parse(imageUrl)
         );
         request.setTitle("Downloading image");
         request.setDescription("Saving image...");
         request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
         );
         request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            fileName
         );
         request.setAllowedOverMetered(true);
         request.setAllowedOverRoaming(true);

         DownloadManager downloadManager = (DownloadManager) getSystemService(
            Context.DOWNLOAD_SERVICE
         );
         downloadManager.enqueue(request);
      });
   }
}
