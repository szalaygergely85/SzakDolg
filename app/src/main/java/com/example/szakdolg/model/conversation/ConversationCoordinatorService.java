package com.example.szakdolg.model.conversation;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.conversation.api.ConversationApiHelper;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.conversation.service.ConversationService;
import com.example.szakdolg.model.user.entity.User;

import java.util.List;

public class ConversationCoordinatorService extends BaseService {

    private ConversationService conversationService;
    private ConversationApiHelper conversationApiHelper;
    public ConversationCoordinatorService(Context context, User currentUser) {
        super(context, currentUser);
        this.conversationService = new ConversationService(context, currentUser);
        this.conversationApiHelper = new ConversationApiHelper(context, currentUser);
    }

    public Conversation getConversation(Long conversationId) {
        Conversation conversation = conversationService.getConversation(conversationId);
        if(conversation==null){
            conversationApiHelper.getConversation(conversationId, conversationRemote -> {
                if(conversationRemote!=null){
                    conversationService.addConversation(conversationRemote);
                }
            });
            return null;
        }else {
            return conversation;
        }
    }
}
