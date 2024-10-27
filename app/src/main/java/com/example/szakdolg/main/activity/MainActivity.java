package com.example.szakdolg.main.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.main.helper.MainActivityHelper;

public class MainActivity extends BaseActivity {

   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;
   private MainActivityHelper _mainActivityHelper;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      this._mainActivityHelper = new MainActivityHelper(this, token, userId);
   }

   @Override
   protected void onStart() {
      super.onStart();
      _mainActivityHelper.startCacheChecking(token, currentUser);
   }

   @Override
   public void onRequestPermissionsResult(
      int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults
   ) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      switch (requestCode) {
         case READ_PERMISSION_CODE:
            if (
               grantResults.length > 0 &&
               grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
               // Permission granted
               Toast
                  .makeText(this, "Read permission granted", Toast.LENGTH_SHORT)
                  .show();
            } else {
               // Permission denied
               Toast
                  .makeText(this, "Read permission denied", Toast.LENGTH_SHORT)
                  .show();
            }
            break;
         case WRITE_PERMISSION_CODE:
            if (
               grantResults.length > 0 &&
               grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
               // Permission granted
               Toast
                  .makeText(
                     this,
                     "Write permission granted",
                     Toast.LENGTH_SHORT
                  )
                  .show();
            } else {
               // Permission denied
               Toast
                  .makeText(this, "Write permission denied", Toast.LENGTH_SHORT)
                  .show();
            }
            break;
      }
   }
}
