package com.zen_vy.chat.activity.contacts.activity;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.contacts.adapter.SearchContactAdapter;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.models.user.util.UserUtil;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchContactsActivity extends BaseActivity {

   private SearchView searchView;
   private RecyclerView contsRecView;
   private List<User> userList = new ArrayList<>();
   private SearchContactAdapter contactsAdapter;
   private UserService userService;

   private void initViews() {
      contsRecView = findViewById(R.id.recvContactSearch);
      searchView = findViewById(R.id.search_view);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search_contacts);

      MaterialToolbar toolbar = findViewById(R.id.search_contacts_Toolbar);
      toolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );

      contactsAdapter = new SearchContactAdapter(this, currentUser);
      userService = new UserService(this);

      initViews();
   }

   @Override
   public boolean onSupportNavigateUp() {
      getOnBackPressedDispatcher().onBackPressed();
      return true;
   }

   @Override
   protected void onStart() {
      super.onStart();
      contactsAdapter.setUserList(userList);
      contsRecView.setAdapter(contactsAdapter);
      contsRecView.setLayoutManager(new LinearLayoutManager(this));

      searchView.setOnQueryTextListener(
         new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               if (newText.length() >= 3) {
                  userService.searchUser(
                     newText,
                     currentUser,
                     new UserService.UserCallback<List<User>>() {
                        @Override
                        public void onSuccess(List<User> users) {
                           UserUtil.removeCurrentUser(
                              users,
                              currentUser.getUserId()
                           );
                           if (!users.isEmpty()) {
                              Collections.sort(
                                 users,
                                 new Comparator<User>() {
                                    @Override
                                    public int compare(User o1, User o2) {
                                       return o1
                                          .getDisplayName()
                                          .compareToIgnoreCase(
                                             o2.getDisplayName()
                                          );
                                    }
                                 }
                              );
                           }

                           contactsAdapter.setUserList(users);
                        }

                        @Override
                        public void onError(Throwable t) {}
                     }
                  );
               } else {
                  if (contactsAdapter.getItemCount() > 0) {
                     contactsAdapter.setUserList(new ArrayList<>());
                  }
               }

               return false;
            }
         }
      );
   }
}
