package com.example.szakdolg.db.retrofit;

public interface CustomCallback<T> {
   void onSuccess(T result);
   void onError(Exception e);
}
