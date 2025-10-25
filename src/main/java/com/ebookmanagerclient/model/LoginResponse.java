package com.ebookmanagerclient.model;

public class LoginResponse {
    private String token;
    private User user;

    // Constructor

    public LoginResponse(){}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
