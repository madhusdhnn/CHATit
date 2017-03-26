package com.example.geekymad.blog;

public class ChatMessages {

    public String text;
    public String email;
    public String photoUrl;

     // Default constructor needed for firebase implements
    public ChatMessages(){}

    public ChatMessages(String text, String email, String photoUrl){
        this.text = text;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getText(){
        return text;
    }

    public String getEmail(){
        return email;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
