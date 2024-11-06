package com.example.szakdolg.model.conversation.api;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.db.retrofit.RetrofitClient;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.user.entity.User;
import java.util.List;
import java.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationParticipantApiHelper extends BaseService {

   private ConversationParticipantApiService conversationParticipantApiService =
      RetrofitClient
         .getRetrofitInstance()
         .create(ConversationParticipantApiService.class);

   public ConversationParticipantApiHelper(Context context, User currentUser) {
      super(context, currentUser);
   }

   public void getParticipants(
      Long conversationId,
      Consumer<List<ConversationParticipant>> onSuccess
   ) {
      Call<List<ConversationParticipant>> call =
         conversationParticipantApiService.getConversationParticipants(
            conversationId,
            currentUser.getAuthToken()
         );
      call.enqueue(
         new Callback<List<ConversationParticipant>>() {
            @Override
            public void onResponse(
               Call<List<ConversationParticipant>> call,
               Response<List<ConversationParticipant>> response
            ) {
               if (response.isSuccessful()) {
                  if (response.body().size() > 0) {
                     List<ConversationParticipant> participants =
                        response.body();
                     if (participants != null) {
                        onSuccess.accept(participants);
                     }
                  }
               }
            }

            @Override
            public void onFailure(
               Call<List<ConversationParticipant>> call,
               Throwable t
            ) {
               Log.e(AppConstants.LOG_TAG, t.getMessage());
            }
         }
      );
   }
}
