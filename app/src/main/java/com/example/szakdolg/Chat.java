package com.example.szakdolg;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    private String id;
    private String contact;
    private String message;
    private final int isFromMe;
    private final int isRead;
    private final int isUploaded;

    public Chat(String id, String contact, String message, int isFromMe, int isRead, int isUploaded) {
        this.id = id;
        this.message = message;
        this.contact = contact;
        this.isFromMe = isFromMe;
        this.isRead = isRead;
        this.isUploaded = isUploaded;
    }

    public Map getHashMap() {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("time", id);
        messageMap.put("contact", contact);
        messageMap.put("message", message);
        messageMap.put("isFromMe", isFromMe);
        messageMap.put("isRead", isRead);
        messageMap.put("isUploaded", isUploaded);

        return messageMap;
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

}
