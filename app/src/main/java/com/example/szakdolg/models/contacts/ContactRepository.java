package com.example.szakdolg.models.contacts;

import com.example.szakdolg.DTO.ContactsDTO;
import java.util.List;
import retrofit2.Callback;

public interface ContactRepository {
   void addContact(
      Contact contact,
      String authToken,
      Callback<Contact> callback
   );

   void deleteContact(
      Contact contact,
      String authToken,
      Callback<Void> callback
   );

   void getContact(
      Long contactId,
      String authToken,
      Callback<Contact> callback
   );

   void getContacts(
      String authToken,
      String search,
      Callback<List<ContactsDTO>> callback
   );

   void updateContact(
      Contact contact,
      String authToken,
      Callback<Void> callback
   );
}
