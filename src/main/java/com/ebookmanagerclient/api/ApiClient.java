package com.ebookmanagerclient.api;

import okhttp3.*;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.util.Map;

/*
 * 
 */

public class ApiClient {
    
    public enum ServiceType
    {
        USER,// 8080        
        BOOK,//8081 
        HIGHLIGHT,//8082
        USERBOOK //8083
    }
    private static ApiClient instance;

    private final OkHttpClient httpClient;
    private final Gson gson;
    private String authToken;

    // Map uses to store key and url
    private final Map<ServiceType, String> serviceUrls;
    public static final MediaType JSON = MediaType.get("application/json; charset = utlf-8");
    
    
    
    
    // Constructor
    private ApiClient()
    {   
        this.gson = new Gson();
        this.serviceUrls = new EnumMap<>(ServiceType.class); // Dùng EnumMap cho hiệu quả

        // Đọc file config
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("src/target/classes/config/app.properties");
            props.load(fis);

            // (THAY ĐỔI) Đọc cả 4 URL và nạp vào Map
            loadServiceUrl(props, ServiceType.USER, "api.baseUrl.user");
            loadServiceUrl(props, ServiceType.BOOK, "api.baseUrl.book");
            loadServiceUrl(props, ServiceType.HIGHLIGHT, "api.baseUrl.highlight");
            loadServiceUrl(props, ServiceType.USERBOOK, "api.baseUrl.userbook");

        } catch (IOException e) {
            throw new RuntimeException("Cannot read app.properties", e);
        }

        this.httpClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request originalRequest = chain.request();
                
                // Các endpoint đăng nhập/đăng ký không cần token
                String urlString = originalRequest.url().toString();
                if (authToken == null || urlString.contains("/login") 
                || urlString.contains("/register")) {
                    return chain.proceed(originalRequest);
                }

                Request authorizedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + authToken)
                        .build();
                
                return chain.proceed(authorizedRequest);
            })
            .build();
    }


    
    
    
    // Read URL from config and insert into Map

    private void loadServiceUrl(Properties props, ServiceType type, String key) {
        String url = props.getProperty(key);
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException(key + " không được tìm thấy trong app.properties");
        }
        this.serviceUrls.put(type, url);
    } 

    
    
    
    
    
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearAuthToken() {
        this.authToken = null;
    }


    
    // <T> is generic typs and based on how user declares
    // Example: If I want to return a class, this function will return 
    // a class
    
    public <T> T get(ServiceType serviceType, 
    String endpoint, Class<T> classOfT) throws IOException {
        String baseUrl = serviceUrls.get(serviceType); // Lấy đúng URL
        Request request = new Request.Builder()
                .url(baseUrl + endpoint) // Ghép URL
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleApiError(response);
            }
            String jsonBody = response.body().string();
            return gson.fromJson(jsonBody, classOfT);
        }
    }
    

    public <T> T post(ServiceType serviceType, String endpoint,
    Object body, Class<T> classOfT) throws IOException {
        String baseUrl = serviceUrls.get(serviceType); // Lấy URL 
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + endpoint) // Ghép URL
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleApiError(response);
            }
            String responseJson = response.body().string();
            return gson.fromJson(responseJson, classOfT);
        }
    }


    public <T> T put(ServiceType serviceType, String endpoint, 
    Object body, Class<T> classOfT) throws IOException
    {
        // take url from service-url map
        String baseUrl = serviceUrls.get(serviceType);
        
        // convert a object to JSON type
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder().url(baseUrl+endpoint)
        .put(requestBody).build();

        // Send request
        try(Response res = httpClient.newCall(request).execute())
        {
            if(!res.isSuccessful())
            {
                handleApiError(res);
            }
            String responseJson = res.body().toString();

            // Pass JSON content into the target object (classOfT)
            return gson.fromJson(responseJson, classOfT);
        }
    }

    public <T> T delete(ServiceType serviceType, String endpoint,
    Object body, Class<T> classOfT) throws IOException {
    String baseUrl = serviceUrls.get(serviceType);
    String jsonBody = gson.toJson(body);

    RequestBody requestBody= RequestBody.create(jsonBody, JSON);
    Request request = new Request.Builder()
            .url(baseUrl + endpoint)
            .delete(requestBody) 
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful()) {
            handleApiError(response);
        }
        String responseJson = response.body().string();
        return gson.fromJson(responseJson, classOfT);
    }
}

    public void downloadFile(ServiceType serviceType, 
    String endpoint, String destinationPath) throws IOException {
        String url;
        // Nếu endpoint đã là URL đầy đủ (bắt đầu bằng http) thì dùng nó
        if (endpoint.startsWith("http")) {
            url = endpoint;
        } else {
            // Nếu không, ghép nó với base URL của dịch vụ
            String baseUrl = serviceUrls.get(serviceType);
            url = baseUrl + endpoint;
        }

        Request request = new Request.Builder().url(url).build();
                
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleApiError(response);
            }
            
            try (InputStream in = response.body().byteStream();
                 OutputStream out = new FileOutputStream(destinationPath)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public <T> T upload(ServiceType serviceType, String endPoint,
    File file, Map<String, String> metadata, Class<T> classOfT)
    throws IOException
    {
        String baseUrl = serviceUrls.get(serviceType);

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();


        // ADD metadata into body of request
        if (metadata != null)
        {
            for(Map.Entry<String, String> entry:metadata.entrySet())
            {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        if(file!=null && file.exists())
        {
            String fileName = file.getName();
            RequestBody fileBody = RequestBody.create(
                file,
                MediaType.parse("application/epub+zip")
            );

            multipartBuilder.addFormDataPart("ebookContent", 
            fileName, fileBody);
        }
        else
        {
            throw new IOException("File does not exist");
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
        .url(baseUrl + endPoint)
        .post(requestBody).build();

        try(Response res = httpClient.newCall(request).execute())
        {
            if(!res.isSuccessful())
            {
                handleApiError(res);
            }
            String resJson = res.body().string();
            return gson.fromJson(resJson, classOfT);
        }
    }
    
    
    
    
    
    private void handleApiError(Response res) throws IOException
    {
        int code = res.code();
        String body = res.body() != null ? res.body().toString() 
        : "No error";

        if (code == 401) {
            clearAuthToken();
            throw new IOException("Lỗi xác thực (401). Token không hợp lệ hoặc đã hết hạn. " + body);
        }
        if (code == 404) {
            throw new IOException("Không tìm thấy tài nguyên (404). " + body);
        }
        if (code == 400) {
            throw new IOException("Yêu cầu không hợp lệ (400). " + body);
        }
        if (code == 500)
        {
            throw new IOException("Lỗi máy chủ (500)." + body);
        }    
        if (code == 403)
        {
            throw new IOException("Khong có quyền truy cập (403)." + body);
        }
        if (code == 409)
        {
            throw new IOException("Trùng lặp tài nguyên (409)." + body);
        }    
    }
}
