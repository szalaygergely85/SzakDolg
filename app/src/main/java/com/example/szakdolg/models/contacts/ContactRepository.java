package com.example.szakdolg.models.contacts;

import java.util.List;

import retrofit2.Callback;

public interface ContactRepository {

    void addContact(Contact contact, String authToken, Callback<Contact> callback);

    void deleteContact(Long contactId, String authToken, Callback<Void> callback);

    void getContact(Long contactId, String authToken, Callback<Contact> callback);

    void getContacts(String authToken, String search, Callback<List<Contact>> callback);

    void updateContact(Contact contact, String authToken, Callback<Void> callback);
}
