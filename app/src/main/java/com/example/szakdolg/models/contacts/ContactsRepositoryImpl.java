package com.example.szakdolg.models.contacts;

import android.content.Context;
import com.example.szakdolg.DTO.ContactsDTO;
import com.example.szakdolg.models.contacts.db.ContactDatabaseUtil;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsRepositoryImpl implements ContactRepository {

   private final ContactApiService contactApiService;
   private final ContactDatabaseUtil contactDatabaseUtil;

   private final UserDatabaseUtil userDatabaseUtil;
   private final Context context;
   private final User currentUser;

   public ContactsRepositoryImpl(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.contactDatabaseUtil = new ContactDatabaseUtil(context, currentUser);
      this.contactApiService =
      RetrofitClient.getRetrofitInstance().create(ContactApiService.class);
      this.userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
   }

   @Override
   public void addContact(
      Contact contact,
      String authToken,
      Callback<Contact> callback
   ) {
      contactApiService
         .addContact(contact, authToken)
         .enqueue(
            new Callback<Contact>() {
               @Override
               public void onResponse(
                  Call<Contact> call,
                  Response<Contact> response
               ) {
                  contactDatabaseUtil.insertContact(contact);
                  callback.onResponse(null, Response.success(contact));
               }

               @Override
               public void onFailure(Call<Contact> call, Throwable throwable) {}
            }
         );
   }

   @Override
   public void deleteContact(
      Contact contact,
      String authToken,
      Callback<Void> callback
   ) {
      // Delete from local database
      // Also delete from the API
      contactApiService
         .deleteContact(contact.getContactUserId(), authToken)
         .enqueue(
            new Callback<Void>() {
               @Override
               public void onResponse(
                  Call<Void> call,
                  Response<Void> response
               ) {
                  contactDatabaseUtil.deleteContact(contact.getContactId());
                  if (callback != null) {
                     callback.onResponse(call, response);
                  }
               }

               @Override
               public void onFailure(Call<Void> call, Throwable throwable) {
                  if (callback != null) {
                     callback.onFailure(call, throwable);
                  }
               }
            }
         );
   }

   @Override
   public void getContact(
      Long contactId,
      String authToken,
      Callback<Contact> callback
   ) {
      // Try to get from local database first
      Contact contact = contactDatabaseUtil.getContact(contactId);
      if (contact != null) {
         callback.onResponse(null, Response.success(contact)); // Return local data
      } else {
         // Fetch from API
         contactApiService
            .getContact(contactId, authToken)
            .enqueue(
               new Callback<Contact>() {
                  @Override
                  public void onResponse(
                     Call<Contact> call,
                     Response<Contact> response
                  ) {
                     if (response.isSuccessful() && response.body() != null) {
                        // Save to local database
                        contactDatabaseUtil.insertContact(response.body());
                        callback.onResponse(call, response);
                     } else {
                        callback.onFailure(
                           call,
                           new Throwable("Failed to fetch contact")
                        );
                     }
                  }

                  @Override
                  public void onFailure(Call<Contact> call, Throwable t) {
                     callback.onFailure(call, t);
                  }
               }
            );
      }
   }

   @Override
   public void getContacts(
      String authToken,
      String search,
      Callback<List<ContactsDTO>> callback
   ) {
      // Get from local database first
      /*
		List<Contact> localContacts = contactDatabaseUtil.getContacts(search);
		if (!localContacts.isEmpty()) {
			callback.onResponse(null, Response.success(localContacts));  // Return local data immediately


		} else {*/
      // Fetch from API
      contactApiService
         .getContacts(authToken, search)
         .enqueue(
            new Callback<List<ContactsDTO>>() {
               @Override
               public void onResponse(
                  Call<List<ContactsDTO>> call,
                  Response<List<ContactsDTO>> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                     // Save contacts to local database
                     for (ContactsDTO contactsDTO : response.body()) {
                        contactDatabaseUtil.insertContact(
                           contactsDTO.getContact()
                        );
                        userDatabaseUtil.insertUser(contactsDTO.getUser());
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Failed to fetch contacts")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<List<ContactsDTO>> call,
                  Throwable t
               ) {
                  callback.onFailure(call, t);
               }
            }
         );
   }

   @Override
   public void updateContact(
      Contact contact,
      String authToken,
      Callback<Void> callback
   ) {
      // Update in local database
      contactDatabaseUtil.updateContact(contact);

      // Also update on the API
      contactApiService.updateContact(contact, authToken).enqueue(callback);
   }
}
