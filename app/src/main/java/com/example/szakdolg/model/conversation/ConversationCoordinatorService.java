package com.example.szakdolg.model.conversation;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.conversation.service.ConversationService;
import com.example.szakdolg.model.user.entity.User;

public class ConversationCoordinatorService extends BaseService {

    private ConversationService conversationService;
    public ConversationCoordinatorService(Context context, User currentUser) {
        super(context, currentUser);
        this.conversationService = new ConversationService(context, currentUser);
    }
}
