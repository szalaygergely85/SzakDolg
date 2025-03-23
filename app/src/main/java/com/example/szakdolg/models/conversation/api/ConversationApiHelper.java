package com.example.szakdolg.models.conversation.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.cache.CacheAction;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.message.api.MessageApiHelper;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.util.UserUtil;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.util.CacheUtil;
import com.example.szakdolg.util.UUIDUtil;
import java.util.List;
import java.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationApiHelper extends BaseService {

   private final String TAG = "ConversationApiHelper";

   private MessageApiHelper messageApiHelper;

   private ConversationApiService conversationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(ConversationApiService.class);

   public ConversationApiHelper(Context context, User currentUser) {
      super(context, currentUser);
      this.messageApiHelper = new MessageApiHelper(context, currentUser);
   }


}
