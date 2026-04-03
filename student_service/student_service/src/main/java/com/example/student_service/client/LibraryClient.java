package com.example.student_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LibraryClient {

    private final RestTemplate restTemplate;
    private final String libraryServiceUrl = "http://library-service:8082";

    public LibraryClient() {
        this.restTemplate = new RestTemplate();
    }

    public void createLibraryAccount(String studentId, String username) {
        try {
            restTemplate.postForObject(
                    libraryServiceUrl + "/api/library/account?studentId=" + studentId + "&username=" + username,
                    null,
                    Void.class
            );
        } catch (Exception e) {
            System.out.println("Library Service unavailable - skipping account creation");
        }
    }
}
