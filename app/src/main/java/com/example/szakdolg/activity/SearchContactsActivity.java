package com.example.szakdolg.activity;

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
import com.example.szakdolg.adapter.SearchContactAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.contacts.ContactsApiService;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchContactsActivity extends AppCompatActivity {

   private EditText search;
   private RecyclerView contsRecView;
   private List<User> contactList;
   private SearchContactAdapter contactsAdapter;

   private String _token;
   private User user;
   private static final String TAG = "SearchContactsActivity";

   private void initViews() {
      search = findViewById(R.id.edtContSearch);
      contsRecView = findViewById(R.id.recvContactSearch);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search_contacts);

      Toolbar mToolbar = (Toolbar) findViewById(R.id.contactsearchBoardToolbar);
      setSupportActionBar(mToolbar);
      //toolbar settings

      _token =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USERTOKEN
      );

      user =
      (User) this.getIntent()
         .getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);
      contactsAdapter = new SearchContactAdapter(this, user, _token);

      ActionBar actionBar = getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle("People Search");

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
      contactList = new ArrayList<>();
      contactsAdapter.setContactList(contactList);
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
                  ContactsApiService contactsApiService = RetrofitClient
                     .getRetrofitInstance()
                     .create(ContactsApiService.class);

                  Call<List<User>> contactsCall =
                     contactsApiService.searchContacts(
                        editable.toString(),
                        _token
                     );

                  contactsCall.enqueue(
                     new Callback<List<User>>() {
                        @Override
                        public void onResponse(
                           Call<List<User>> call,
                           Response<List<User>> response
                        ) {
                           if (response.isSuccessful()) {
                              Log.e(TAG, "" + response.code());

                              List<User> contactList = response.body();
                              contactsAdapter.setContactList(contactList);
                           }
                        }

                        @Override
                        public void onFailure(
                           Call<List<User>> call,
                           Throwable t
                        ) {
                           Log.e(TAG, "" + t.getMessage());
                        }
                     }
                  );
               }
            }
         }
      );
   }
}
