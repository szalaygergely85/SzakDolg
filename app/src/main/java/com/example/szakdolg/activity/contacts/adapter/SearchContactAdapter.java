package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.models.user.entity.User;

import java.util.ArrayList;
import java.util.List;

public class SearchContactAdapter
   extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder>
{


   private final Context context;

   private User currentUser;
   private List<User> userList = new ArrayList<>();



   public SearchContactAdapter(
      Context context,
      User currentUser
   ) {
      this.currentUser = currentUser;
      this.context = context;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.item_contact_search, parent, false);
      SearchContactAdapter.ViewHolder holder =
         new SearchContactAdapter.ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User user = userList.get(holder.getAdapterPosition());

      holder.txtName.setText(user.getDisplayName());

      /*
      holder.btnAdd.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                  context,
                  currentUser
               );

               ContactApiService contactApiService = RetrofitClient
                  .getRetrofitInstance()
                  .create(ContactApiService.class);

               Call<User> contactsCall = contactApiService.addContact(
                  currentUser.getUserId(),
                  userSearchResult.getUserId(),
                  _token
               );

               contactsCall.enqueue(
                  new Callback<User>() {
                     @Override
                     public void onResponse(
                        Call<User> call,
                        Response<User> response
                     ) {
                        if (response.isSuccessful()) {
                           User user = response.body();
                           if (user != null) {
                              if (
                                 userDatabaseUtil.getUserById(
                                    user.getUserId()
                                 ) ==
                                 null
                              ) {
                                 userDatabaseUtil.insertUser(user);
                              }
                           }
                        }
                        Log.e(
                           AppConstants.LOG_TAG,
                                AppConstants.LOG_TAG + " " + response.code()
                        );
                     }

                     @Override
                     public void onFailure(Call<User> call, Throwable t) {
                        Log.e(AppConstants.LOG_TAG, AppConstants.LOG_TAG + " " + t.getMessage());
                     }
                  }
               );
            }
         }
      );*/
   }

   @Override
   public int getItemCount() {
      return userList.size();
   }

   public void setUserList(List<User> userList) {
      this.userList = userList;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView txtName;
      private final Button btnAdd;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         imageView = itemView.findViewById(R.id.imgContItem);
         txtName = itemView.findViewById(R.id.txtContItemName);
         btnAdd = itemView.findViewById(R.id.btnContItemAdd);
      }
   }
}
