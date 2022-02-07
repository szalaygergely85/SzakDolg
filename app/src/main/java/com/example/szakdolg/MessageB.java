package com.example.szakdolg;

public class MessageB {
    private String name;
    private String message;
    private String imageUrl;

    public MessageB(String name, String message, String imageUrl) {
        this.name = name;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MessageB{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
