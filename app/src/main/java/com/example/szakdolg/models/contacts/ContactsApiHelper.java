package com.example.szakdolg.models.contacts;

@Deprecated
public class ContactsApiHelper {
/*
   ContactApiService contactApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ContactApiService.class);

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
         contactApiService.getContactsAndCompareWithLocal(
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
      Call<List<User>> contactsCall = contactApiService.getContacts(userToken);

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
   }*/
}
