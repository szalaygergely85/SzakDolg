package com.example.szakdolg.contacts.helper;

import android.content.Context;
import com.example.szakdolg.cache.CacheAction;
import com.example.szakdolg.contacts.service.ContactsApiService;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsApiHelper {

   ContactsApiService contactsApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ContactsApiService.class);

   public void checkCachedContacts(
      String token,
      Context context,
      User currentUser
   ) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      Call<ArrayList<User>> call =
         contactsApiService.getContactsAndCompareWithLocal(
            userDatabaseUtil.getUserCount(),
            token
         );
      call.enqueue(
         new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(
               Call<ArrayList<User>> call,
               Response<ArrayList<User>> response
            ) {
               if (response.isSuccessful()) {
                  if (response.body().size() > 0) {
                     CacheAction.validateContacts(
                        response.body(),
                        context,
                        currentUser
                     );
                  }
               }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {}
         }
      );
   }

   public interface ContactsCallback {
      void onContactsFetched(List<User> newContacts);
   }

   public void getContactsToMultiTextView(
      Context context,
      String userToken,
      ContactsCallback callback
   ) {
      Call<List<User>> contactsCall = contactsApiService.getContacts(userToken);

      contactsCall.enqueue(
         new Callback<List<User>>() {
            @Override
            public void onResponse(
               Call<List<User>> call,
               Response<List<User>> response
            ) {
               if (response.isSuccessful()) {
                  List<User> contactList = response.body();
                  if (callback != null) {
                     callback.onContactsFetched(contactList);
                  }
               }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
         }
      );
   }
}
