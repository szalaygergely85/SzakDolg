package com.example.szakdolg.model.user.model;

import java.io.Serializable;

public class User implements Serializable {

   private Long userId;
   private String displayName;
   private String email;

   private String password;

   private Long userTokenId;
   private String publicKey;

   public User(
      Long userId,
      String displayName,
      String email,
      String password,
      Long userTokenId) {
      this.userId = userId;
      this.displayName = displayName;
      this.email = email;
      this.password = password;
      this.userTokenId = userTokenId;
   }

   public User(
      String displayName,
      String email,
      String password
   ) {
      this.displayName = displayName;
      this.email = email;
      this.password = password;
   }

   public User(
           Long userId,
      String displayName,
      String publicKey,
      String email
   ) {
      this.userId = userId;
      this.displayName = displayName;
      this.publicKey = publicKey;
      this.email = email;
   }

   public User(
      Long userId,
      String displayName,
      String email
   ) {
      this.userId = userId;
      this.displayName = displayName;
      this.email = email;

   }

   public User(Long userId) {
      this.userId = userId;
   }

   public User(
      String displayName,
      String email,
      String hashPass,
      String publicKey
   ) {
      this.displayName = displayName;
      this.email = email;
      this.password = hashPass;
      this.publicKey = publicKey;
   }

    public User() {

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


   public String getPublicKey() {
      return publicKey;
   }

   public void setPublicKey(String publicKey) {
      this.publicKey = publicKey;
   }
}
