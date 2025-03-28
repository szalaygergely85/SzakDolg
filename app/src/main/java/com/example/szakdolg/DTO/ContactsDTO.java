package com.example.szakdolg.DTO;

import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.user.entity.User;

public class ContactsDTO {
    private Contact contact;
    private User user;

    public ContactsDTO() {
    }

    public ContactsDTO(Contact contact, User user) {
        this.contact = contact;
        this.user = user;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
