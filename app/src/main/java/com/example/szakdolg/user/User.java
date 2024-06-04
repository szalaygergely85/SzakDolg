package com.example.szakdolg.user;

import java.io.Serializable;

public class User implements Serializable {
    private Long userId;
    private String surName;
    private String firstName;
    private String email;

    private String password;

    private UserToken userToken;
    private Long phoneNumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(Long userId, String surName, String firstName, String email, UserToken userToken, Long phoneNumber) {
        this.userId = userId;
        this.surName = surName;
        this.firstName = firstName;
        this.email = email;
        this.userToken = userToken;
        this.phoneNumber = phoneNumber;
    }
}
