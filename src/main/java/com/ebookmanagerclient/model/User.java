package com.ebookmanagerclient.model;

import java.io.Serial;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private int id;
    @SerializedName("username")
    private String username;

    public User(){}

    public User(int id, String username)
    {
        this.id = id;
        this.username = username;
    }

    // Getters

    public int getId()
    { 
        return id; 
    }

    public String getUsername() 
    { 
        return username;
    }

    // Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override

    public String toString()
    {
        return "User{" +
                "id= " + id +
                ", username= " + username +
                "\'}";
    }
    
}
