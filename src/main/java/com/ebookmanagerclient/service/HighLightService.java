package com.ebookmanagerclient.service;

import com.ebookmanagerclient.api.ApiClient;
import com.ebookmanagerclient.api.ApiClient.ServiceType;
import com.ebookmanagerclient.model.HighLight;
import java.io.IOException;
import java.util.Map;


public class HighLightService {
    
    private ApiClient apiClient;

    public HighLightService()
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

    public HighLight createHighLight(int bookId, HighLight newHighlight)
    throws IOException
    {
        
        return apiClient.post(
            ServiceType.HIGHLIGHT,
            "/user/books/" + bookId + "/highlights",
            newHighlight,
            HighLight.class
        );
    }

    public void deleteHightlight(int bookId, int highlightId) 
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
