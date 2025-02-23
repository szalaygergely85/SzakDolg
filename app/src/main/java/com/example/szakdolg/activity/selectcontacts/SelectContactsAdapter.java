package com.example.szakdolg.activity.selectcontacts;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.models.user.entity.User;


public class SelectContactsAdapter extends RecyclerView.Adapter<SelectContactsAdapter.ViewHolder>{

    private final Context context;
    private final User currentUser;

    public SelectContactsAdapter(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public SelectContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectContactsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
