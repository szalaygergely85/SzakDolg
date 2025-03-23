package com.example.szakdolg.models.conversation.repository;

import android.content.Context;

import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.models.conversation.api.ConversationApiService;
import com.example.szakdolg.models.conversation.db.ConversationDatabaseUtil;
import com.example.szakdolg.models.conversation.db.ConversationParticipantDatabaseUtil;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationRepositoryImpl implements ConversationRepository {

    private final Context context;
    private final User currentUser;
    private final ConversationDatabaseUtil conversationDatabaseUtil;
    private final ConversationApiService conversationApiService;
    private final UserDatabaseUtil userDatabaseUtil;
    private final ConversationParticipantDatabaseUtil conversationParticipantDatabaseUtil;

    public ConversationRepositoryImpl(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
        this.conversationDatabaseUtil = new ConversationDatabaseUtil(context, currentUser);
        this.conversationApiService = RetrofitClient.getRetrofitInstance().create(ConversationApiService.class);
        this.userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
        this.conversationParticipantDatabaseUtil = new ConversationParticipantDatabaseUtil(context, currentUser);
    }


    @Override
    public void addConversation(Conversation conversation, String token, Callback<Conversation> callback) {

        //TODO Check locally if Conversation exists

        conversationApiService.addConversation(conversation, token).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {

                conversationDatabaseUtil.insertConversation(response.body());

                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch contact"));
            }
        });

    }

    @Override
    public void addConversationByUserId(List<Long> userIds, String token, Callback<ConversationDTO> callback) {
        conversationApiService.addConversationByUserId(userIds, token).enqueue(new Callback<ConversationDTO>() {
            @Override
            public void onResponse(Call<ConversationDTO> call, Response<ConversationDTO> response) {
               insertConversationDTO(response.body());

                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ConversationDTO> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch contact"));
            }
        });
    }

    @Override
    public void getConversation(Long id, String token, Callback<ConversationDTO> callback) {
        Conversation localConversation = conversationDatabaseUtil.getConversationById(id);
        if (localConversation != null) {
            List<ConversationParticipant> conversationParticipants = conversationParticipantDatabaseUtil.getParticipantsByConversationId(localConversation.getConversationId());
            if (conversationParticipants != null) {
                List<User> users = new ArrayList<>();
                for (ConversationParticipant conversationParticipant : conversationParticipants) {
                    User user = userDatabaseUtil.getUserById(conversationParticipant.getUserId());
                    if (user != null) {
                        users.add(user);
                    } else {
                        getConversationDTOFromApi(id, token, callback);
                        break;
                    }

                }
                callback.onResponse(null, Response.success(new ConversationDTO(localConversation, conversationParticipants, users)));
            } else {
                getConversationDTOFromApi(id, token, callback);
            }

        } else {
            getConversationDTOFromApi(id, token, callback);
        }

    }

    @Override
    public void getAllConversation(String token, Callback<List<ConversationDTO>> callback) {
        conversationApiService.getAllConversation(token).enqueue(new Callback<List<ConversationDTO>>() {
            @Override
            public void onResponse(Call<List<ConversationDTO>> call, Response<List<ConversationDTO>> response) {
                for (ConversationDTO conversationDTO : response.body()) {
                    if (conversationDTO!=null) {
                        insertConversationDTO(conversationDTO);
                    }
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<List<ConversationDTO>> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch contact"));
            }
        });
    }

    private void getConversationDTOFromApi(Long id, String token, Callback<ConversationDTO> callback) {
        conversationApiService.getConversation(id, token).enqueue(new Callback<ConversationDTO>() {
            @Override
            public void onResponse(Call<ConversationDTO> call, Response<ConversationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ConversationDTO conversationDTO = response.body();
                    if (conversationDTO != null) {
                        insertConversationDTO(conversationDTO);
                    }
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Failed to fetch conversation"));
                }
            }

            @Override
            public void onFailure(Call<ConversationDTO> call, Throwable throwable) {
                callback.onFailure(call, new Throwable("Failed to fetch conversation"));
            }
        });
    }

    private void insertConversationDTO(ConversationDTO conversationDTO) {
        Conversation conversation = conversationDTO.getConversation();
        if (conversation != null) {
            conversationDatabaseUtil.insertConversation(conversationDTO.getConversation());
        }

        for (ConversationParticipant conversationParticipant : conversationDTO.getParticipants()) {
            if (conversationParticipant != null) {
                conversationParticipantDatabaseUtil.insertConversationParticipant(conversationParticipant);
            }
        }

        for (User user : conversationDTO.getUsers()) {
            if (user != null) {
                userDatabaseUtil.insertUser(user);
            }
        }
    }
}
