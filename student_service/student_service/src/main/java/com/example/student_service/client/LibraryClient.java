package com.example.student_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *  HTTP client responsible for all communication between the Student Service and the
 *  Library Service.
 */

@Component // Registers this class as a Spring bean so it can be injected.
public class LibraryClient {

    // Built in HTTP client used to make REST calls to other services
    private final RestTemplate restTemplate;

    // Base URL for the Library Service.
    private final String libraryServiceUrl = "http://library-service:8082";

    // Manually instantiates RestTemplate.
    public LibraryClient() {
        this.restTemplate = new RestTemplate();
    }

    public void createLibraryAccount(String studentId, String username) {
        try {
            // POST to /api/library/account with studentId and username as query params.
            restTemplate.postForObject(
                    libraryServiceUrl + "/api/library/account?studentId=" + studentId + "&username=" + username,
                    null, // No request body.
                    Void.class // No Response needed.
            );
        } catch (Exception e) {
            // If Library Service is down skips over creation of account.
            System.out.println("Library Service unavailable - skipping account creation");
        }
    }
}
