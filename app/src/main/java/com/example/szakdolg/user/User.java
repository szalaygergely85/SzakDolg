package com.example.szakdolg.user;

import java.io.Serializable;

public class User implements Serializable {
    private Long userId;
    private String surName;
    private String firstName;
    private String email;

    private String password;

    private Long userTokenId;
    private Long phoneNumber;

    public User(Long userId, String surName, String firstName, String email, String password, Long userTokenId, Long phoneNumber) {
        this.userId = userId;
        this.surName = surName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.userTokenId = userTokenId;
        this.phoneNumber = phoneNumber;
    }

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

    public Long getUserTokenId() {
        return userTokenId;
    }

    public void setUserTokenId(Long userTokenId) {
        this.userTokenId = userTokenId;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
