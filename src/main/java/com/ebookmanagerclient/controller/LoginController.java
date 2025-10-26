package com.ebookmanagerclient.controller;

import com.ebookmanagerclient.service.AuthService;
import com.ebookmanagerclient.model.User;

import java.io.IOException;

public class LoginController {
    
    private final AuthService authService;

    public LoginController()
    {
        this.authService = new AuthService();
    }

    // Execute login request from Login UI

    public boolean handleLogin(String username, String password)
    {
        return authService.login(username, password);
    }

    public boolean handleRegister(String username, String password)
    {
        try
        {
            User newUser = authService.register(username, password);
            return (newUser != null);
        }
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
 
    // Return current User on app
    public User getCurrentUser()
    {
        return authService.getCurrentUser();
    }
}
