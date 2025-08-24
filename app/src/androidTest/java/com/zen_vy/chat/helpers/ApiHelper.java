package com.zen_vy.chat.helpers;

import android.content.Context;
import com.google.gson.Gson;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import java.io.IOException;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class ApiHelper {

   private static final String BASE_URL = AppConstants.API_URL;
   private static final MediaType JSON = MediaType.get(
      "application/json; charset=utf-8"
   );
   private static final Gson gson = new Gson();
   private static final OkHttpClient client = new OkHttpClient();

   public static User addUser(User user) throws IOException {
      String json = gson.toJson(user);

      RequestBody body = RequestBody.create(json, JSON);
      Request request = new Request.Builder()
         .url(BASE_URL + "user")
         .post(body)
         .build();

      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful()) {
            throw new IOException(
               "Unexpected code " + response.code() + ": " + response.message()
            );
         }

         return gson.fromJson(response.body().charStream(), User.class);
      }
   }

   public static User getUserByToken(String token) throws IOException {
      Request request = new Request.Builder()
         .url(BASE_URL + "user/token/" + token)
         .build();

      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful()) {
            throw new IOException(
               "Unexpected code " + response.code() + ": " + response.message()
            );
         }

         return gson.fromJson(response.body().charStream(), User.class);
      }
   }

   public static ConversationDTO addConversationByUserIds(
      List<Long> userIds,
      String token
   ) throws IOException {
      String json = gson.toJson(userIds);

      RequestBody body = RequestBody.create(json, JSON);
      Request request = new Request.Builder()
         .url(BASE_URL + "conversation/add-conversation/user-ids")
         .post(body)
         .addHeader("Authorization", token)
         .build();

      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful()) {
            throw new IOException(
               "Unexpected code " + response.code() + ": " + response.message()
            );
         }

         return gson.fromJson(
            response.body().charStream(),
            ConversationDTO.class
         );
      }
   }

   public static MessageEntry addMessage(MessageEntry message, String token)
      throws IOException {
      OkHttpClient client = new OkHttpClient();
      Gson gson = new Gson();

      String json = gson.toJson(message);

      RequestBody body = RequestBody.create(
         json,
         MediaType.get("application/json")
      );
      Request request = new Request.Builder()
         .url(BASE_URL + "message/add-message")
         .addHeader("Authorization", token)
         .post(body)
         .build();

      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
         }

         return gson.fromJson(response.body().charStream(), MessageEntry.class);
      }
   }

   public static void deleteMessage(
      MessageEntry messageEntry,
      Context context,
      User user
   ) {
      MessageService messageService = new MessageService(context, user);
      messageService.deleteMessage(
         messageEntry,
         new MessageService.MessageCallback<List<MessageEntry>>() {
            @Override
            public void onSuccess(List<MessageEntry> data) {}

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   public static void deleteUser(String email, Context context) {
      UserService userService = new UserService(context);
      userService.deleteUser(
         email,
         TestUtil.TEST_UUID,
         new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
               Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   public static void deleteUser(Long userId, Context context) {
      UserService userService = new UserService(context);
      userService.deleteUser(
         userId,
         TestUtil.TEST_UUID,
         new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
               Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   public static void deleteConversation(
      long conversationId,
      User testUser,
      Context context
   ) {
      ConversationService conversationService = new ConversationService(
         context,
         testUser
      );
      conversationService.deleteConversation(
         conversationId,
         testUser.getToken(),
         new ConversationService.ConversationCallback<Void>() {
            @Override
            public void onSuccess(Void data) {}

            @Override
            public void onError(Throwable t) {}
         }
      );
   }
}
