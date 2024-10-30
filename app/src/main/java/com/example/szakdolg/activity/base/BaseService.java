package com.example.szakdolg.activity.base;

import android.content.Context;

import com.example.szakdolg.model.user.entity.User;

public class BaseService {
    protected Context context;
    protected User currentUser;

    public BaseService(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }


}