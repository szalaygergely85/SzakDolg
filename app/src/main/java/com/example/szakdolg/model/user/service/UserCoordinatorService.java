package com.example.szakdolg.model.user.service;

import android.content.Context;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.entity.User;

public class UserCoordinatorService extends BaseService {

    private UserService userService;
    private UserApiHelper userApiHelper;

    public UserCoordinatorService(Context context, User currentUser) {
        super(context, currentUser);
        this.userService = new UserService(context);
        this.userApiHelper = new UserApiHelper();
    }

    public User getUserByUserId(Long userId, User currentUser) {
        User user = userService.getUserByUserId(userId, currentUser);
        if(user!=null){
            return user;
        }else {
            userApiHelper.getUserByUserID(userId, currentUser.getAuthToken(), user1 -> userService.addUser(user1, currentUser));
            return null;
        }
    }
}
