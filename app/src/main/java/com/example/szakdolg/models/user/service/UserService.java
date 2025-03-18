package com.example.szakdolg.models.user.service;

import android.content.Context;

import com.example.szakdolg.DTO.LoginRequest;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.entity.UserToken;
import com.example.szakdolg.models.user.repository.UserRepository;
import com.example.szakdolg.models.user.repository.UserRepositoryImpl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

    private Context context;

    private UserRepository userRepository;

    public UserService(Context context) {
        this.context = context;
        this.userRepository = new UserRepositoryImpl(context);
    }

    public User addUser(User user, User currentUser) {
        User newUser = new User();
        newUser.setUserId(user.getUserId());
        newUser.setDisplayName(user.getDisplayName());
        newUser.setEmail(user.getEmail());
        newUser.setPublicKey(user.getPublicKey());
        newUser.setProfilePictureUuid(user.getProfilePictureUuid());
        newUser.setStatus(user.getStatus());
        newUser.setTags(user.getTags());
        newUser.setAuthToken(user.getAuthToken());

        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                context,
                currentUser
        );
        userDatabaseUtil.insertUser(newUser);
        return newUser;
    }

    public User getUserByUserId(Long userId, User currentUser) {
        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                context,
                currentUser
        );
        return userDatabaseUtil.getUserById(userId);
    }

    public void updateUser(User user, User currentUser) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }
        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                context,
                currentUser
        );
        userDatabaseUtil.updateUser(user);
    }

    public List<User> getAllUser(User currentUser) {
        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
                context,
                currentUser
        );
        return userDatabaseUtil.getAllUsers();
    }


    // new Repository starting from here

    public void getTokenByPasswordAndEmail(
            String hashPassword,
            String email,
            final UserService.UserCallback<UserToken> callback
    ) {
        userRepository.getTokenByPasswordAndEmail(new LoginRequest(email, hashPassword), new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void getUserByToken(String token, final UserService.UserCallback<User> callback) {
        userRepository.getUserByToken(token, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });

    }

    public void getUserByUserId(Long userId, User currentUser, final UserService.UserCallback<User> callback) {
        userRepository.getUserByID(userId, currentUser.getAuthToken(), new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void searchUser(String search, User user, final UserService.UserCallback<List<User>> callback) {
        userRepository.searchUser(search, user.getAuthToken(), new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void addUser(User user, final UserService.UserCallback<User> callback) {
        userRepository.addUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update contact"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });

    }

    public interface UserCallback<T> {
        void onSuccess(T data);

        void onError(Throwable t);
    }

}
