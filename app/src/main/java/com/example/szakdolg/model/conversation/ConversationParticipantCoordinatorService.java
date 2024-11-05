package com.example.szakdolg.model.conversation;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.conversation.api.ConversationParticipantApiHelper;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.conversation.service.ConversationParticipantService;
import com.example.szakdolg.model.user.entity.User;

import java.util.List;

public class ConversationParticipantCoordinatorService extends BaseService {
    private ConversationParticipantService conversationParticipantService;
    private ConversationParticipantApiHelper conversationParticipantApiHelper;
    public ConversationParticipantCoordinatorService(Context context, User currentUser) {
        super(context, currentUser);
        this.conversationParticipantService =
                new ConversationParticipantService(context, currentUser);
        this.conversationParticipantApiHelper = new ConversationParticipantApiHelper(context, currentUser);
    }

    public List<ConversationParticipant> getOtherParticipants(Long conversationId) {
        List<ConversationParticipant> participants = conversationParticipantService.getOtherParticipants(conversationId);
        if(participants.size()<1){
            conversationParticipantApiHelper.getParticipants(conversationId);
            return null;
        }else {
            return participants;
        }
    }
}
