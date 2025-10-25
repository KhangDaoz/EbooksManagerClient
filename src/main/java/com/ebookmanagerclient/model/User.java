package com.ebookmanagerclient.model;

public class User {
    private int id;
    private String username;

    public User(){}

    public User(int id, String username)
    {
        this.id = id;
        this.username = username;
    }

    // Getters

    public int getId(){ return id; }

    public String getUsername() { return username ;}

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
