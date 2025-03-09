package com.example.szakdolg.activity.selectcontacts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView txtName;
        private final TextView txtEmail;
        private final TextView txtPhone;
        private final RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtContName);
            txtEmail = itemView.findViewById(R.id.txtContEmail);
            txtPhone = itemView.findViewById(R.id.txtContPhone);
            relativeLayout = itemView.findViewById(R.id.relLayContact);
            imageView = itemView.findViewById(R.id.imgCont);
        }
    }
}
