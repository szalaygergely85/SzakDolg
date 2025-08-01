package com.zen_vy.chat.models.contacts;

import android.content.Context;
import com.zen_vy.chat.DTO.ContactsDTO;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactService {

   private final ContactRepository contactsRepository;

   private User currentUser;

   public ContactService(Context context, User currentUser) {
      this.currentUser = currentUser;
      this.contactsRepository = new ContactRepositoryImpl(context, currentUser);
   }

   // Add a contact (from UI)
   public void addContact(
      Contact contact,
      String authToken,
      final ContactCallback<Contact> callback
   ) {
      contactsRepository.addContact(
         contact,
         authToken,
         new Callback<Contact>() {
            @Override
            public void onResponse(
               Call<Contact> call,
               Response<Contact> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to add contact"));
               }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   // Delete a contact (from UI)
   public void deleteContact(
      Contact contact,
      final ContactCallback<Void> callback
   ) {
      contactsRepository.deleteContact(
         contact,
         currentUser.getToken(),
         new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to delete contact"));
               }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   // Get a single contact (from UI)
   public void getContact(
      Long contactId,
      String authToken,
      final ContactCallback<Contact> callback
   ) {
      contactsRepository.getContact(
         contactId,
         authToken,
         new Callback<Contact>() {
            @Override
            public void onResponse(
               Call<Contact> call,
               Response<Contact> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to get contact"));
               }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   // Get a list of contacts (from UI)
   public void getContacts(
      String authToken,
      String search,
      final ContactCallback<List<ContactsDTO>> callback
   ) {
      contactsRepository.getContacts(
         authToken,
         search,
         new Callback<List<ContactsDTO>>() {
            @Override
            public void onResponse(
               Call<List<ContactsDTO>> call,
               Response<List<ContactsDTO>> response
            ) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to get contacts"));
               }
            }

            @Override
            public void onFailure(Call<List<ContactsDTO>> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   // Update a contact (from UI)
   public void updateContact(
      Contact contact,
      String authToken,
      final ContactCallback<Void> callback
   ) {
      contactsRepository.updateContact(
         contact,
         authToken,
         new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
               if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
               } else {
                  callback.onError(new Throwable("Failed to update contact"));
               }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   public void isContact(
      Long userId,
      String authToken,
      final ContactCallback<Boolean> callback
   ) {
      contactsRepository.getContacts(
         authToken,
         null,
         new Callback<List<ContactsDTO>>() {
            @Override
            public void onResponse(
               Call<List<ContactsDTO>> call,
               Response<List<ContactsDTO>> response
            ) {
               if (response.isSuccessful() && response.body() != null) {
                  boolean found = false;

                  for (ContactsDTO contactsDTO : response.body()) {
                     Contact contact = contactsDTO.getContact();
                     if (userId.equals(contact.getContactUserId())) {
                        found = true;
                        break;
                     }
                  }
                  callback.onSuccess(found);
               } else {
                  callback.onError(new Throwable("Failed to get contacts"));
               }
            }

            @Override
            public void onFailure(Call<List<ContactsDTO>> call, Throwable t) {
               callback.onError(t);
            }
         }
      );
   }

   // Callback interface to handle success and error responses
   public interface ContactCallback<T> {
      void onSuccess(T data);
      void onError(Throwable t);
   }
}
