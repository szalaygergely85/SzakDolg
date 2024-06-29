package com.example.szakdolg.user.entity;

import java.io.Serializable;
import java.util.Date;

public class UserToken implements Serializable {

   private String token;
   private Date generationDate;
   private Date expirationDate;

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public Date getGenerationDate() {
      return generationDate;
   }

   public void setGenerationDate(Date generationDate) {
      this.generationDate = generationDate;
   }

   public Date getExpirationDate() {
      return expirationDate;
   }

   public void setExpirationDate(Date expirationDate) {
      this.expirationDate = expirationDate;
   }

   public UserToken(String token, Date generationDate, Date expirationDate) {
      this.token = token;
      this.generationDate = generationDate;
      this.expirationDate = expirationDate;
   }
}
