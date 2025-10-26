package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;
import com.ebookmanagerclient.model.HighLight;
import java.io.IOException;
import java.util.Map;


public class HighlightService {
    
    private ApiClient apiClient;

    public HighlightService()
    {
        this.apiClient = ApiClient.getInstance();
    }

    public HighLight[] getHighlightsForBook(int bookId) throws IOException
    {
        return apiClient.get(
            ServiceType.HIGHLIGHT,
            "/user/books/" + bookId + "/highlights",
            HighLight[].class
        );
    }

    public HighLight createHighlight(int bookId, HighLight newHighlight)
    throws IOException
    {
        
        return apiClient.post(
            ServiceType.HIGHLIGHT,
            "/user/books/" + bookId + "/highlights",
            newHighlight,
            HighLight.class
        );
    }

    public void deleteHighlight(int bookId, int highlightId) 
    throws IOException
    {
        String endpoint = "/user/books/" + bookId + "/highlights";

        Map<String, Integer> requestBody = Map.of(
            "highlight_id", highlightId
        );

        
        apiClient.delete(
            ServiceType.HIGHLIGHT, 
            endpoint,
            requestBody,       
            Object.class       
        );
    }

}
