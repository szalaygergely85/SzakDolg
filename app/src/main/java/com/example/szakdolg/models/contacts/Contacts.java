package com.example.szakdolg.models.contacts;



public class Contacts {
   private Long contactId;
   private Long ownerId;
   private Long contactUserId;
   public Contacts(Long contactId, Long ownerId, Long contactUserId) {
      this.contactId = contactId;
      this.ownerId = ownerId;
      this.contactUserId = contactUserId;
   }

   public Contacts() {}

   public Contacts(Long ownerId, Long contactUserId) {
      this.ownerId = ownerId;
      this.contactUserId = contactUserId;
   }

   public Long getContactId() {
      return contactId;
   }

   public void setContactId(Long contactId) {
      this.contactId = contactId;
   }

   public Long getOwnerId() {
      return ownerId;
   }

   public void setOwnerId(Long ownerId) {
      this.ownerId = ownerId;
   }

   public Long getContactUserId() {
      return contactUserId;
   }

   public void setContactUserId(Long contactUserId) {
      this.contactUserId = contactUserId;
   }
}
