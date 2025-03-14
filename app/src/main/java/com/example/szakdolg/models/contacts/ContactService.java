package com.example.szakdolg.models.contacts;

import android.content.Context;

import com.example.szakdolg.models.user.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactService {
    private final ContactsRepositoryImpl contactsRepository;

    public ContactService(Context context, User currentUser) {
        this.contactsRepository = new ContactsRepositoryImpl(context, currentUser);
    }

    // Add a contact (from UI)
    public void addContact(Contact contact, String authToken, final ContactCallback<Contact> callback) {
        contactsRepository.addContact(contact, authToken, new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
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
        });
    }

    // Delete a contact (from UI)
    public void deleteContact(Long contactId, String authToken, final ContactCallback<Void> callback) {
        contactsRepository.deleteContact(contactId, authToken, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Throwable("Failed to delete contact"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Get a single contact (from UI)
    public void getContact(Long contactId, String authToken, final ContactCallback<Contact> callback) {
        contactsRepository.getContact(contactId, authToken, new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
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
        });
    }

    // Get a list of contacts (from UI)
    public void getContacts(String authToken, String search, final ContactCallback<List<Contact>> callback) {
        contactsRepository.getContacts(authToken, search, new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to get contacts"));
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Update a contact (from UI)
    public void updateContact(Contact contact, String authToken, final ContactCallback<Void> callback) {
        contactsRepository.updateContact(contact, authToken, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Callback interface to handle success and error responses
    public interface ContactCallback<T> {
        void onSuccess(T data);
        void onError(Throwable t);
    }

}
