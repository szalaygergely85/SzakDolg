package com.zen_vy.chat.models.message;

import android.content.Context;

import com.zen_vy.chat.activity.base.BaseService;
import com.zen_vy.chat.models.message.entity.MessageStatus;
import com.zen_vy.chat.models.user.entity.User;

public class MessageStatusService extends BaseService {

    private MessageStatusDatabaseUtil messageStatusDatabaseUtil;

    private MessageService messageService;

    public MessageStatusService(Context context, User currentUser) {
        super(context, currentUser);
        this.messageStatusDatabaseUtil = new MessageStatusDatabaseUtil(context, currentUser);
        this.messageService = new MessageService(context, currentUser);
    }


    public MessageStatus getMessageStatus(String uuid) {
       return messageStatusDatabaseUtil.getMessageStatus(uuid);
    }
}
