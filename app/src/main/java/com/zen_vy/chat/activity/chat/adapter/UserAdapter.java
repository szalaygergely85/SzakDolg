package com.zen_vy.chat.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> implements Filterable {

   private List<User> originalUsers;
   private List<User> filteredUsers;

   public UserAdapter(
      @NonNull Context context,
      int resource,
      @NonNull List<User> objects
   ) {
      super(context, resource, objects);
      this.originalUsers = new ArrayList<>(objects);
      this.filteredUsers = new ArrayList<>(objects);
   }

   @Override
   public int getCount() {
      return filteredUsers.size();
   }

   @Nullable
   @Override
   public User getItem(int position) {
      return filteredUsers.get(position);
   }

   @NonNull
   @Override
   public View getView(
      int position,
      @Nullable View convertView,
      @NonNull ViewGroup parent
   ) {
      if (convertView == null) {
         convertView =
         LayoutInflater
            .from(getContext())
            .inflate(android.R.layout.simple_list_item_1, parent, false);
      }
      TextView textView = convertView.findViewById(android.R.id.text1);
      textView.setText(getItem(position).getDisplayName());
      return convertView;
   }

   @NonNull
   @Override
   public Filter getFilter() {
      return new Filter() {
         @Override
         protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<User> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
               suggestions.addAll(originalUsers);
            } else {
               String filterPattern = constraint
                  .toString()
                  .toLowerCase()
                  .trim();
               for (User user : originalUsers) {
                  if (
                     user.getDisplayName().toLowerCase().contains(filterPattern)
                  ) {
                     suggestions.add(user);
                  }
               }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
         }

         @Override
         protected void publishResults(
            CharSequence constraint,
            FilterResults results
         ) {
            filteredUsers.clear();
            if (results != null && results.count > 0) {
               filteredUsers.addAll((List<User>) results.values);
            }
            notifyDataSetChanged();
         }

         @Override
         public CharSequence convertResultToString(Object resultValue) {
            return ((User) resultValue).getDisplayName();
         }
      };
   }

   public void updateUsers(List<User> newUsers) {
      originalUsers.clear();
      originalUsers.addAll(newUsers);
      filteredUsers.clear();
      filteredUsers.addAll(newUsers);
      notifyDataSetChanged();
   }
}
