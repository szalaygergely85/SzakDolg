package com.zen_vy.chat;

import android.content.Context;

import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.RandomUtil;

import timber.log.Timber;

public class TestUtil {
    private static final String TEST_UUID = "test_123456";
    public static void deleteUser(Long userId, Context context){
        UserService userService = new UserService(context);
        userService.deleteUser(userId, TEST_UUID, new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    public static void addUser(String email, Context context){
        UserService userService = new UserService(context);
       userService.addUser(new User(
                       RandomUtil.getRandomString(5),
                       email,
                       RandomUtil.getRandomString(6),
                       RandomUtil.getRandomString(6),
                       RandomUtil.getRandomString(6),
                       RandomUtil.getRandomString(6),
                       RandomUtil.getRandomLong(),
                       RandomUtil.getRandomString(6)

               ), new UserService.LoginCallback<User>() {
                   @Override
                   public void onSuccess(User data) {

                   }

                   @Override
                   public void onUserNotFound() {

                   }

                   @Override
                   public void onError(Throwable t) {

                   }
               }
       );

    }




    public static String createRandomEmail(){
        return RandomUtil.getRandomString(5) + "@" + RandomUtil.getRandomString(3)+".com";
    }

    public static void deleteUser(String email, Context context) {
        UserService userService = new UserService(context);
        userService.deleteUser(email, TEST_UUID, new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }
}
