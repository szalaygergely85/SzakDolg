package com.example.szakdolg.retrofit;

import com.example.szakdolg.models.user.entity.UserToken;

public interface SuccessCallBack {
    void onSuccess();
    void addUserSuccess(UserToken userToken);
}