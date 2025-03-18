package com.example.szakdolg.activity.contacts.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.contacts.adapter.SearchContactAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.contacts.ContactApiService;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserService;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchContactsActivity extends BaseActivity {

   private EditText search;
   private RecyclerView contsRecView;
   private List<User> userList = new ArrayList<>();;
   private SearchContactAdapter contactsAdapter;
   private UserService userService;

   private void initViews() {
      search = findViewById(R.id.edtContSearch);
      contsRecView = findViewById(R.id.recvContactSearch);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_search_contacts);

      contactsAdapter = new SearchContactAdapter(this, currentUser);
      userService = new UserService(this);

      initViews();
   }
   @Override
   public boolean onSupportNavigateUp() {
      onBackPressed();
      return super.onSupportNavigateUp();
   }

   @Override
   protected void onStart() {
      super.onStart();
      contactsAdapter.setUserList(userList);
      contsRecView.setAdapter(contactsAdapter);
      contsRecView.setLayoutManager(new LinearLayoutManager(this));

      search.addTextChangedListener(
         new TextWatcher() {
            private CharSequence previousText = "";

            @Override
            public void beforeTextChanged(
               CharSequence charSequence,
               int start,
               int count,
               int after
            ) {
               previousText = charSequence;
            }

            @Override
            public void onTextChanged(
               CharSequence charSequence,
               int start,
               int before,
               int count
            ) {}
            @Override
            public void afterTextChanged(Editable editable) {
               if (editable.length() >= 3) {
                   userService.searchUser(editable.toString(), currentUser, new UserService.UserCallback<List<User>>() {
                       @Override
                       public void onSuccess(List<User> users) {
                           contactsAdapter.setUserList(users);
                       }
                       @Override
                       public void onError(Throwable t) {
                       }
                   });

                   }

            }
         }
      );
   }
}