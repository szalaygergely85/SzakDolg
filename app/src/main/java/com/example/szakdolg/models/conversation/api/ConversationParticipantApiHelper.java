package com.example.szakdolg.models.conversation.api;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;
import java.util.List;
import java.util.function.Consumer;
import okhttp3.ResponseBody;
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
            currentUser.getToken()
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

   public void addParticipants(
      List<ConversationParticipant> conversationParticipants
   ) {
      Call<ResponseBody> call =
         conversationParticipantApiService.addConversationParticipants(
            conversationParticipants,
            currentUser.getToken()
         );
      call.enqueue(
         new Callback<ResponseBody>() {
            @Override
            public void onResponse(
               Call<ResponseBody> call,
               Response<ResponseBody> response
            ) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
         }
      );
   }
}
