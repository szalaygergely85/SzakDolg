package com.example.szakdolg.message;

import android.util.Log;
import android.widget.Adapter;

import androidx.appcompat.app.ActionBar;

import com.example.szakdolg.recviewadapter.ChatAdapter;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageApiHelper {
    private final String TAG ="MessageApiHelper";

    private MessageApiService messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);


    public void reloadMessages(Long conversationId, ChatAdapter adapter, ActionBar actionBar){



        Call<ArrayList<MessageEntry>> call = messageApiService.getConversationMessages(conversationId);

        call.enqueue(new Callback<ArrayList<MessageEntry>>(){
            @Override
            public void onResponse(Call<ArrayList<MessageEntry>> call, Response<ArrayList<MessageEntry>> response) {
                Log.e(TAG, ""+response.code());

                if (response.isSuccessful()) {
                    ArrayList<MessageEntry> messageEntryList = response.body();
                    if (messageEntryList!=null){

                        adapter.setMessageEntries(messageEntryList);


                    }

                } else {
                    Log.e(TAG, ""+response.code());
                    //TODO Handle the error
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MessageEntry>> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });

    }
    public void sendMessage(Long conversationId, MessageEntry messageEntry, ChatAdapter adapter){

        Call<MessageEntry> call = messageApiService.sendMessage(messageEntry);

        call.enqueue(new Callback<MessageEntry>(){
            @Override
            public void onResponse(Call<MessageEntry> call, Response<MessageEntry> response) {
                Log.e(TAG, ""+response.code());

                if (response.isSuccessful()) {
                    Log.e(TAG, ""+response.body());

                    reloadMessages(conversationId, adapter, null);

                } else {
                    Log.e(TAG, ""+response.code());
                    //TODO Handle the error
                }
            }
            @Override
            public void onFailure(Call<MessageEntry> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });




    }

}
