package com.example.szakdolg.models.message.api;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.chat.adapter.ChatAdapter;
import com.example.szakdolg.models.message.MessageDatabaseUtil;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageApiHelper {

   private final String TAG = "MessageApiHelper";

   private Context context;
   private User currentUser;

   private MessageApiService messageApiService;

   public MessageApiHelper(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.messageApiService =
      RetrofitClient.getRetrofitInstance().create(MessageApiService.class);
   }

   public void reloadMessages(
      Context context,
      Long conversationId,
      ChatAdapter adapter,
      User user
   ) {
      /*
	MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
		context,
		user
	);
	adapter.setMessageEntries(
		messageDatabaseUtil.getAllMessageEntriesByConversationId(
			conversationId
		)
	);*/
   }

   public void sendMessage(
      Context context,
      Long conversationId,
      MessageEntry messageEntry,
      ChatAdapter adapter,
      String userToken,
      User user
   ) {
      Call<MessageEntry> call = messageApiService.addMessage(
         messageEntry,
         userToken
      );
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         user
      );

      call.enqueue(
         new Callback<MessageEntry>() {
            @Override
            public void onResponse(
               Call<MessageEntry> call,
               Response<MessageEntry> response
            ) {
               Log.e(TAG, "" + response.code());

               if (response.isSuccessful()) {
                  if (response.body() != null) {
                     MessageEntry message = response.body();
                     messageDatabaseUtil.insertMessageEntry(message);

                     reloadMessages(context, conversationId, adapter, user);
                  }
               } else {
                  Log.e(TAG, "" + response.code());
                  //TODO Handle the error
               }
            }

            @Override
            public void onFailure(Call<MessageEntry> call, Throwable t) {
               Log.e(TAG, "" + t.getMessage());
            }
         }
      );
   }
}
