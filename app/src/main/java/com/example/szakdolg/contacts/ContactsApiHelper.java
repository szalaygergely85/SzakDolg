package com.example.szakdolg.contacts;

import android.content.Context;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsApiHelper {

   ContactsApiService contactsApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ContactsApiService.class);

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
