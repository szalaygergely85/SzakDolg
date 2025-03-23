package com.example.szakdolg.models.user.entity;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

   private Long id;
   private Long userId;
   private String displayName;
   private String email;
   private String password;
   private String publicKey;
   private String profilePictureUuid;
   private String status;
   private String tags;
   private String token;

   public User(
      Long id,
      Long userId,
      String displayName,
      String email,
      String publicKey,
      String profilePictureUuid,
      String status,
      String tags,
      String token
   ) {
      this.id = id;
      this.userId = userId;
      this.displayName = displayName;
      this.email = email;

      this.publicKey = publicKey;
      this.profilePictureUuid = profilePictureUuid;
      this.status = status;
      this.tags = tags;
      this.token = token;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true; // Check for reference equality
      if (obj == null || getClass() != obj.getClass()) return false; // Check for null or different class
      User other = (User) obj;
      return userId.equals(other.userId); // Compare only by ID
   }

   @Override
   public int hashCode() {
      return Objects.hash(userId); // Hash based on ID
   }

   public User(
      String displayName,
      String email,
      String password,
      String publicKey,
      String status,
      String tags
   ) {
      this.displayName = displayName;
      this.email = email;
      this.password = password;
      this.publicKey = publicKey;
      this.status = status;
      this.tags = tags;
   }

   public User(String displayName, String email, String password) {
      this.displayName = displayName;
      this.email = email;
      this.password = password;
   }

   public User(Long id, String displayName, String email) {
      this.id = id;
      this.displayName = displayName;
      this.email = email;
   }

   public User() {}

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
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

   public String getPublicKey() {
      return publicKey;
   }

   public void setPublicKey(String publicKey) {
      this.publicKey = publicKey;
   }

   public String getProfilePictureUuid() {
      return profilePictureUuid;
   }

   public void setProfilePictureUuid(String profilePictureUuid) {
      this.profilePictureUuid = profilePictureUuid;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
