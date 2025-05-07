package com.zen_vy.chat.retrofit;

public interface CustomCallback<T> {
   void onSuccess(T result);
   void onError(Exception e);
}
