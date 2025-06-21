package com.zen_vy.chat.activity.image;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.constans.IntentConstants;
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
      //TODO Make it look better
      // Bind views
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

      adapter = new FullscreenImageAdapter(this, imageUrls, currentUser);

      viewPager.setAdapter(adapter);

      viewPager.setCurrentItem(imageUrls.indexOf(currentImageUrl));

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
         String currentUrl = imageUrls.get(viewPager.getCurrentItem());
         //TODO DOWNLOAD

      });
   }
}
