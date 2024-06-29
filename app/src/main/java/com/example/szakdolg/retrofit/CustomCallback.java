package com.example.szakdolg.retrofit;

public interface CustomCallback<T> {
   void onSuccess(T result);
   void onError(Exception e);
}
