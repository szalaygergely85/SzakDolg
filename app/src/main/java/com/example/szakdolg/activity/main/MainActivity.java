package com.example.szakdolg.activity.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.main.adapter.MainAdapter;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends BaseActivity {

   private RecyclerView messageBoardRecView;
   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;

   private LinearLayout emptyLayout;
   private LinearLayout withItemsLayout;

   private MaterialToolbar topAppBar;
   private MainActivityHelper _mainActivityHelper;
   private BottomNavigationView bottomNavigationView;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      _initView();
      //_setListeners();
   }

   @Override
   protected void onStart() {
      super.onStart();

      this._mainActivityHelper = new MainActivityHelper(this, currentUser);

      _mainActivityHelper.setBottomNavMenu(bottomNavigationView);
      _mainActivityHelper.setTopBarMenu(topAppBar);

      MainAdapter mainAdapter = new MainAdapter(this, currentUser);
      List<Conversation> conversations =
         _mainActivityHelper.getConversationList();
      if (!conversations.isEmpty()) {

         emptyLayout.setVisibility(View.GONE);
         withItemsLayout.setVisibility(View.VISIBLE);
         mainAdapter.setConversationList(conversations);
         messageBoardRecView.setAdapter(mainAdapter);
         messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));
      } else {
         emptyLayout.setVisibility(View.VISIBLE);
         withItemsLayout.setVisibility(View.GONE);
      }
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

   private void _initView() {
      topAppBar = findViewById(R.id.topAppBar);
      messageBoardRecView = findViewById(R.id.messageBoardRecView);
      bottomNavigationView = findViewById(R.id.bottom_navigation);
      emptyLayout = findViewById(R.id.llayoutEmptyMain);
      withItemsLayout = findViewById(R.id.llayoutWithItemsMain);
   }
}
