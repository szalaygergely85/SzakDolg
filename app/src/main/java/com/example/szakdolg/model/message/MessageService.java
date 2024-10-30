package com.example.szakdolg.model.message;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.model.user.entity.User;

public class MessageService extends BaseService {

    private MessageDatabaseUtil messageDatabaseUtil;


    public MessageService(Context context, User currentUser) {
        super(context, currentUser);
        this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
    }
}
