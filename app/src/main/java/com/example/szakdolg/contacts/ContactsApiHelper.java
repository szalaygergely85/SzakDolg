package com.example.szakdolg.contacts;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsApiHelper {
    ContactsApiService contactsApiService = RetrofitClient
            .getRetrofitInstance()
            .create(ContactsApiService.class);

    public interface OnContactsLoadedListener {
        void onContactsLoaded(ArrayList<String> loadedContacts);
        void onError(String errorMessage);
    }

public void getContactsToMultiTextView(Context context, String userToken, MultiAutoCompleteTextView recipientInput, OnContactsLoadedListener listener){
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
                        for(User contact : contactList){
                            contacts.add(contact.getDisplayName());
                        }

                        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, contacts);

                        recipientInput.setAdapter(dropdownAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            }
    );
}
}