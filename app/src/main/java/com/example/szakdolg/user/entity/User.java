package com.example.szakdolg.user.entity;

import java.io.Serializable;

public class User implements Serializable {
    private Long userId;
    private String displayName;
    private String fullName;
    private String email;

    private String password;

    private Long userTokenId;
    private Long phoneNumber;
    private String publicKey;


    public User(String displayName, String fullName, String email, String password, Long phoneNumber, String publicKey) {
        this.displayName = displayName;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.publicKey = publicKey;
    }

    public User(Long userId, String displayName, String fullName, String email, String password, Long userTokenId, Long phoneNumber) {
        this.userId = userId;
        this.displayName = displayName;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userTokenId = userTokenId;
        this.phoneNumber = phoneNumber;
    }

    public User(String displayName, String fullName, String email, String password, Long phoneNumber) {
        this.displayName = displayName;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
