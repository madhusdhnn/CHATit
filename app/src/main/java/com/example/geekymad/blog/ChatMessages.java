package com.example.geekymad.blog;


 public class ChatMessages {

    private String text;
    private String email;

     // Default constructor needed for firebase
    public ChatMessages(){}

    public ChatMessages(String text, String email){
        this.text = text;
        this.email = email;

    }

    public String getText(){
        return text;
    }

    public String getEmail(){
        return email;
    }


    public void setText(String text){
        this.text = text;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
