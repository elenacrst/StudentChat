package com.example.elena.studentchat;

/**
 * Created by Elena on 10/25/2017.
 */

public class ChatMessage {
    private String body, name, photoUrl;

    public ChatMessage(){
    }
    public ChatMessage(String text, String name, String photoUrl){
        this.body= text;
        this.name= name;
        this.photoUrl = photoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setBody(String text) {
        this.body = text;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getBody() {
        return body;
    }
}
