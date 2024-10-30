package com.example.szakdolg.activity.contacts.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.user.api.ContactsApiService;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchContactAdapter
   extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder> {

   private static final String TAG = "SearchContactAdapter";
   private final Context context;

   private User currentUser;
   private List<User> contactList = new ArrayList<>();

   private String _token;

   /*
	public void setImageView(String uID, Context context, ImageView image) {
		try {
			storageRef.child(uID + ".jpg").getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
				@Override
				public void onComplete(@NonNull Task<StorageMetadata> task) {
					if (task.isSuccessful()) {
						storageRef.child(uID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri) {
								Log.d(AppConstants.LOG_TAG, TAG + " " "getPicURl: " + uri);
								Glide.with(context)
										.asBitmap()
										.load(uri)
										.into(new CustomTarget<Bitmap>() {
											@Override
											public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
												try {
													FileHandling.saveImageFile(uID, resource, context);
												} catch (IOException e) {
													e.printStackTrace();
												}
											}

											@Override
											public void onLoadCleared(@Nullable Drawable placeholder) {
											}
										});

								Glide.with(context)
										.asBitmap()
										.load(uri)
										.into(image);
							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception exception) {
								Log.d(AppConstants.LOG_TAG, TAG + " " "onFailure: " + exception);
							}
						});
					}
				}
			});

		} catch (Exception e) {
			Log.d(AppConstants.LOG_TAG, TAG + " " "setImageView: " + e);
		}
	}
*/
   public SearchContactAdapter(
      Context context,
      User currentUser,
      String token
   ) {
      this.currentUser = currentUser;
      this.context = context;
      this._token = token;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.contact_search_item, parent, false);
      SearchContactAdapter.ViewHolder holder =
         new SearchContactAdapter.ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User userSearchResult = contactList.get(holder.getAdapterPosition());

      holder.txtEmail.setText(userSearchResult.getEmail());
      // setImageView(contactList.get(position).getID(), mContext, holder.imageView);
      holder.btnAdd.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                  context,
                  currentUser
               );

               ContactsApiService contactsApiService = RetrofitClient
                  .getRetrofitInstance()
                  .create(ContactsApiService.class);

               Call<User> contactsCall = contactsApiService.addContact(
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
                           TAG + " " + response.code()
                        );
                     }

                     @Override
                     public void onFailure(Call<User> call, Throwable t) {
                        Log.e(AppConstants.LOG_TAG, TAG + " " + t.getMessage());
                     }
                  }
               );
            }
         }
      );
   }

   @Override
   public int getItemCount() {
      return contactList.size();
   }

   public void setContactList(List<User> contactList) {
      this.contactList = contactList;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView txtName;
      private final TextView txtEmail;
      private final ImageButton btnAdd;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         imageView = itemView.findViewById(R.id.imgContItem);
         txtName = itemView.findViewById(R.id.txtContItemName);
         txtEmail = itemView.findViewById(R.id.txtContItemEmail);
         btnAdd = itemView.findViewById(R.id.btnContItemAdd);
      }
   }
}
