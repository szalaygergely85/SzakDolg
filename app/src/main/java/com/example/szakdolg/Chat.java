package com.example.szakdolg;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat {
    private String id;
    private String contact;
    private String message;
    private int isFromMe;
    private boolean isRead;
    private boolean isUploaded;

    public Chat(String id, String contact, String message, int isFromMe, boolean isRead, boolean isUploaded) {
        this.id = id;
        this.message = message;
        this.contact = contact;
        this.isFromMe = isFromMe;
        this.isRead = isRead;
        this.isUploaded = isUploaded;
    }

    public Map getHashMap(){
        Map<String, Object> message = new HashMap<>();
        message.put("time", id);
        message.put("contact", contact);
        message.put("message", message);
        message.put("isFromMe", isFromMe);
        message.put("isRead", isRead);
        message.put("isUploaded", isUploaded);

        return message;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", contact='" + contact + '\'' +
                ", isFromMe=" + isFromMe +
                ", isRead=" + isRead +
                ", isUploaded=" + isUploaded +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int isFromMe() {
        return isFromMe;
    }

    public void setFromMe(int fromMe) {
        isFromMe = fromMe;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
