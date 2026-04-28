package com.library_service.library_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Client for the Google books public API.
 *
 * Searches good books API for ISBNs.
 */
@Component
public class GoogleBooksClient {

    // Google API key from the application.properties.
    @Value("${google.books.api-key}")
    private String apiKey;

    // Google books API link.
    @Value("${google.books.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleBooksClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Searches the Google Books API for books matching the given query and returns
     * up to 20 results.
     * @param query search string.
     * @return a list of 20 books.
     */
    public List<Map<String, String>> searchBooks(String query) {
        List<Map<String, String>> books = new ArrayList<>();
        try {
            String url = apiUrl + "/volumes?q=" + query + "&maxResults=20&key=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    JsonNode volumeInfo = item.get("volumeInfo");
                    Map<String, String> book = new HashMap<>();
                    book.put("isbn", getIsbn(volumeInfo));
                    book.put("title", getText(volumeInfo, "title"));
                    book.put("author", getAuthors(volumeInfo));
                    book.put("description", getText(volumeInfo, "description"));
                    book.put("thumbnail", getThumbnail(volumeInfo));
                    books.add(book);
                }
            }
        } catch (Exception e) {
            System.out.println("Google Books API error: " + e.getMessage());
        }
        return books;
    }

    // Extracts the ISBN-13 identifier form books info node.
    private String getIsbn(JsonNode volumeInfo) {
        try {
            JsonNode identifiers = volumeInfo.get("industryIdentifiers");
            if (identifiers != null) {
                for (JsonNode id : identifiers) {
                    if (id.get("type").asText().equals("ISBN_13")) {
                        return id.get("identifier").asText();
                    }
                }
            }
        } catch (Exception e) {}
        return "N/A";
    }

    // Extracts a simple text field from a JsonNode by field name.
    private String getText(JsonNode node, String field) {
        try {
            return node.get(field).asText();
        } catch (Exception e) {
            return "N/A";
        }
    }

    // Extracts author list from books volume info.
    private String getAuthors(JsonNode volumeInfo) {
        try {
            JsonNode authors = volumeInfo.get("authors");
            if (authors != null && authors.isArray()) {
                List<String> authorList = new ArrayList<>();
                for (JsonNode author : authors) {
                    authorList.add(author.asText());
                }
                return String.join(", ", authorList);
            }
        } catch (Exception e) {}
        return "Unknown";
    }

    // Extracts thumbnail image from the volume info.
    private String getThumbnail(JsonNode volumeInfo) {
        try {
            return volumeInfo.get("imageLinks").get("thumbnail").asText();
        } catch (Exception e) {
            return "";
        }
    }
}
