package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;

import com.ebookmanagerclient.model.LoginResponse;
import com.ebookmanagerclient.model.User;

import java.io.IOException;
import java.util.Map;

/*
 * Use this class for Logic Authorizing and Phrase managing
 * 
 *  Controller  -----  AuthService  -----  ApiClient
 */


/*
 * login()
 * logout()
 * register()
 * getcurrentUser()
 * checkUserLoggedIn()
 */
public class AuthService {
    private ApiClient apiClient;
    private User currentUser;
    
    // Take instance of ApiClient

    public AuthService()
    {
        this.apiClient = ApiClient.getInstance();
        this.currentUser = null;
    }

    // Login managing

    public boolean login(String username, String password)
    {
        try
        {
            // Prepare body for request

            Map<String, String> requestBody = Map.of(
                "username", username,
                "password", password
            );

            // Call API to USER port
            
            LoginResponse response = apiClient.post(
                ServiceType.USER,
                "/api/sessions",
                requestBody,
                LoginResponse.class
            );

            // Save session

            if(response != null && response.getToken() != null)
            {
                // Save token into apiClient
                apiClient.setAuthToken(response.getToken());
                
                // Save information of current user
                this.currentUser = response.getUser();
                return true;
            }
            return false;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    // Sign out managing

    public void logout()
    {
        try 
        {
            // Call API /logout
            apiClient.delete(ServiceType.USER, 
            "/api/sessions", null,
            Object.class);    

        } catch (Exception e) {
            e.printStackTrace();    
        }
        finally
        {
            // Clear this session
            apiClient.clearAuthToken();
            this.currentUser = null;
        }

    }

    // Register managing

    public User register(String username, String password) throws 
    IOException
    {
        Map<String, String> requestBody = Map.of( 
            "username", username, 
            "password", password
        );
        
        return apiClient.post
        (
        ServiceType.USER, 
        "/api/users",
        requestBody, 
        User.class
        );
    }

    // Take information of current user
    public User gerCurrentUser()
    {
        return this.currentUser;
    }

    public boolean changePassword(String oldPassword, String newPassword)
    {
        if(!isAuthenticated())
        {
            return false;
        }

        try 
        {
            Map<String, String> requestBody = Map.of(
                "oldPassword", oldPassword,
                "newPassword", newPassword);

            apiClient.put(
                ServiceType.USER,
                "/api/sessions",
                requestBody,
                Object.class
            );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Check if user loged in 
    public boolean isAuthenticated()
    {
        return this.currentUser != null;
    }

    // Take existed Token after restart 
    public boolean fetchCurrentuser()
    {
        try
        {
            User user = apiClient.get
            (
                ServiceType.USER,
                "/api/users",
                User.class
            );
            this.currentUser = user;
            return true;
        }
        catch (IOException e)
        {
            apiClient.clearAuthToken();
            this.currentUser = null;
            return false;
        }
    }
}
