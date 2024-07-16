package com.example.szakdolg.contacts;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.example.szakdolg.chat.activity.NewChatActivity;
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

    public interface ContactsCallback {
        void onContactsFetched(List<User> newContacts);
    }

public void getContactsToMultiTextView(Context context, String userToken, ContactsCallback callback){
    Call<List<User>> contactsCall = contactsApiService.getContacts(userToken);

    contactsCall.enqueue(
            new Callback<List<User>>() {
                @Override
                public void onResponse(
                        Call<List<User>> call,
                        Response<List<User>> response
                ) {
                    if (response.isSuccessful()) {
                        //List<String> contacts = new ArrayList<>();
                        List<User> contactList = response.body();
                        /*
                        for(User contact : contactList){
                            contacts.add(contact.getDisplayName());
                        }*/

                        if (callback!=null){
                            callback.onContactsFetched(contactList);}


                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            }
    );
}
}