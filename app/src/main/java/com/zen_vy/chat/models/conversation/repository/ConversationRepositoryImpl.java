package com.zen_vy.chat.models.conversation.repository;

import android.content.Context;
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.models.conversation.api.ConversationApiService;
import com.zen_vy.chat.models.conversation.db.ConversationDatabaseUtil;
import com.zen_vy.chat.models.conversation.db.ConversationParticipantDatabaseUtil;
import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.entity.ConversationParticipant;
import com.zen_vy.chat.models.message.MessageDatabaseUtil;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.dbutil.UserDatabaseUtil;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.retrofit.RetrofitClient;
import com.zen_vy.chat.util.DateTimeUtil;

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

   private final MessageDatabaseUtil messageDatabaseUtil;

   public ConversationRepositoryImpl(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(context, currentUser);
      this.conversationApiService =
      RetrofitClient.getRetrofitInstance().create(ConversationApiService.class);
      this.userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
      this.conversationParticipantDatabaseUtil =
      new ConversationParticipantDatabaseUtil(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
   }

   @Override
   public void addConversation(
      Conversation conversation,
      String token,
      Callback<Conversation> callback
   ) {

      conversationApiService
         .addConversation(conversation, token)
         .enqueue(
            new Callback<Conversation>() {
               @Override
               public void onResponse(
                  Call<Conversation> call,
                  Response<Conversation> response
               ) {
                  conversationDatabaseUtil.insertConversation(response.body());

                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(
                  Call<Conversation> call,
                  Throwable throwable
               ) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   @Override
   public void addConversationByUserId(
      List<Long> userIds,
      String token,
      Callback<ConversationDTO> callback
   ) {

      List<Conversation> conversationList = conversationDatabaseUtil.getAllConversations();
       for (Conversation conversation : conversationList) {
           if (localConversationExists(userIds, conversation)) {
               getConversation(conversation.getConversationId(), token, new Callback<ConversationDTO>() {
                   @Override
                   public void onResponse(Call<ConversationDTO> call, Response<ConversationDTO> response) {
                       callback.onResponse(call, response);
                   }

                   @Override
                   public void onFailure(Call<ConversationDTO> call, Throwable throwable) {

                   }
               });
return;
           }
       }



      conversationApiService
         .addConversationByUserId(userIds, token)
         .enqueue(
            new Callback<ConversationDTO>() {
               @Override
               public void onResponse(
                  Call<ConversationDTO> call,
                  Response<ConversationDTO> response
               ) {
                  _insertConversationDTO(response.body(), null);

                  callback.onResponse(call, response);
               }

               @Override
               public void onFailure(
                  Call<ConversationDTO> call,
                  Throwable throwable
               ) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

    private boolean localConversationExists(List<Long> userIds, Conversation conversation) {

            if (conversation.getNumberOfParticipants() == userIds.size()){
                List<ConversationParticipant> participants = conversationParticipantDatabaseUtil.getParticipantsByConversationId(conversation.getConversationId());
                for (ConversationParticipant conversationParticipant: participants){
                    if(!userIds.contains(conversationParticipant.getUserId())){
                        return false;
                    }
                }

            }else {
                return false;
            }

   return true;
   }

    @Override
   public void getConversation(
      Long id,
      String token,
      Callback<ConversationDTO> callback
   ) {
      Conversation localConversation =
         conversationDatabaseUtil.getConversationById(id);
      if (localConversation != null && DateTimeUtil.daysFromNow(localConversation.getLastUpdated())<1) {
         List<ConversationParticipant> conversationParticipants =
            conversationParticipantDatabaseUtil.getParticipantsByConversationId(
               localConversation.getConversationId()
            );
         if (conversationParticipants != null) {
            List<User> users = new ArrayList<>();
            for (ConversationParticipant conversationParticipant : conversationParticipants) {
               User user = userDatabaseUtil.getUserById(
                  conversationParticipant.getUserId()
               );
               if (user != null) {
                  users.add(user);
               } else {
                  _getConversationDTOFromApi(id, token, callback, localConversation);
                  break;
               }
            }
            MessageEntry messageEntry =
               messageDatabaseUtil.getLatestMessageEntry(id);
            if (messageEntry != null) {
               callback.onResponse(
                  null,
                  Response.success(
                     new ConversationDTO(
                        localConversation,
                        conversationParticipants,
                        users,
                        messageEntry
                     )
                  )
               );
            } else {
               _getConversationDTOFromApi(id, token, callback, localConversation);
            }
         } else {
            _getConversationDTOFromApi(id, token, callback, localConversation);
         }
      } else {
         _getConversationDTOFromApi(id, token, callback, localConversation);
      }
   }

   @Override
   public void getAllConversation(
      String token,
      Callback<List<ConversationDTO>> callback
   ) {

       //TODO csak kell majd az a updated tabla
       /*


	List<Conversation> localConversations =
		conversationDatabaseUtil.getAllConversations();
	if (!localConversations.isEmpty()) {
		List<ConversationDTO> conversationDTOs = new ArrayList<>();

		for (Conversation conversation : localConversations) {
			List<User> users = new ArrayList<>();
			List<ConversationParticipant> conversationParticipants =
			conversationParticipantDatabaseUtil.getParticipantsByConversationId(
				conversation.getConversationId()
			);
			if (conversationParticipants != null) {
			for (ConversationParticipant conversationParticipant : conversationParticipants) {
				User user = userDatabaseUtil.getUserById(
					conversationParticipant.getUserId()
				);
				if (user != null) {
					users.add(user);
				}
			}
			}
			MessageEntry messageEntry =
			messageDatabaseUtil.getLatestMessageEntry(
				conversation.getConversationId()
			);
			if (messageEntry != null) {
			conversationDTOs.add(
				new ConversationDTO(
					conversation,
					conversationParticipants,
					users,
					messageEntry
				)
			);
			callback.onResponse(null, Response.success(conversationDTOs));
			}
		}

	}*/
      conversationApiService
         .getAllConversation(token)
         .enqueue(
            new Callback<List<ConversationDTO>>() {
               @Override
               public void onResponse(
                  Call<List<ConversationDTO>> call,
                  Response<List<ConversationDTO>> response
               ) {
                  if (response.body() != null) {
                     for (ConversationDTO conversationDTO : response.body()) {
                        if (conversationDTO != null) {
                           MessageEntry messageEntry =
                              conversationDTO.getMessageEntry();
                           if (messageEntry != null) {
                              messageEntry.setContent(
                                 messageEntry.getContentEncrypted()
                              );
                              _insertConversationDTO(conversationDTO, null);
                           }
                        }
                     }
                     callback.onResponse(call, response);
                  } else {
                     callback.onFailure(
                        call,
                        new Throwable("Response body is empty")
                     );
                  }
               }

               @Override
               public void onFailure(
                  Call<List<ConversationDTO>> call,
                  Throwable throwable
               ) {
                  callback.onFailure(
                     call,
                     new Throwable("Failed to fetch contact")
                  );
               }
            }
         );
   }

   private void _getConversationDTOFromApi(
      Long id,
      String token,
      Callback<ConversationDTO> callback,
      Conversation localConversation) {
      conversationApiService
         .getConversation(id, token)
         .enqueue(
            new Callback<ConversationDTO>() {
               @Override
               public void onResponse(
                  Call<ConversationDTO> call,
                  Response<ConversationDTO> response
               ) {
                  if (response.isSuccessful() && response.body() != null) {
                        _insertConversationDTO(response.body(), localConversation);
                     callback.onResponse(call, response);
                  } else {
                      ConversationDTO localDTO = buildLocalConversationDTO(id);
                      if (localDTO != null) {
                          callback.onResponse(null, Response.success(localDTO));
                      } else {
                          callback.onFailure(call, new Throwable("Failed to fetch conversation and build local DTO"));
                      }
                  }
               }

               @Override
               public void onFailure(
                  Call<ConversationDTO> call,
                  Throwable throwable
               ) {
                   ConversationDTO localDTO = buildLocalConversationDTO(id);
                   if (localDTO != null) {
                       callback.onResponse(null, Response.success(localDTO));
                   } else {
                       callback.onFailure(call, new Throwable("Failed to fetch conversation and build local DTO"));
                   }
               }
            }
         );
   }

   private void _insertConversationDTO(ConversationDTO conversationDTO, Conversation localConversation) {
      Conversation conversation = conversationDTO.getConversation();
      if (conversation != null) {
          if(localConversation!=null) {
              if (conversation.getLastUpdated() > localConversation.getLastUpdated())
                  conversationDatabaseUtil.insertConversation(
                          conversation
                  );
          }else {
              conversationDatabaseUtil.insertConversation(
                      conversation);
          }
      }

      for (ConversationParticipant conversationParticipant : conversationDTO.getParticipants()) {
         if (conversationParticipant != null) {
            conversationParticipantDatabaseUtil.insertConversationParticipant(
               conversationParticipant
            );
         }
      }

      for (User user : conversationDTO.getUsers()) {
         if (user != null) {
            userDatabaseUtil.insertUser(user);
         }
      }

      MessageEntry messageEntry = conversationDTO.getMessageEntry();
      if (messageEntry != null) {
         messageDatabaseUtil.insertMessageEntry(messageEntry);
      }
   }

    private ConversationDTO buildLocalConversationDTO(Long conversationId) {
        Conversation conversation = conversationDatabaseUtil.getConversationById(conversationId);
        if (conversation == null) return null;

        List<ConversationParticipant> participants =
                conversationParticipantDatabaseUtil.getParticipantsByConversationId(conversationId);

        if (participants == null) return null;

        List<User> users = new ArrayList<>();
        for (ConversationParticipant participant : participants) {
            User user = userDatabaseUtil.getUserById(participant.getUserId());
            if (user == null) return null;
            users.add(user);
        }

        MessageEntry messageEntry = messageDatabaseUtil.getLatestMessageEntry(conversationId);
        if (messageEntry == null) return null;

        return new ConversationDTO(conversation, participants, users, messageEntry);
    }

}
