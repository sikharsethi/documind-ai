package com.sikhar.documindbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Groq embedding model — 768 dimensions
    private static final String EMBEDDING_MODEL = "nomic-embed-text-v1_5";
    private static final String GROQ_EMBEDDING_URL =
            "https://api.groq.com/openai/v1/embeddings";

    public float[] generateEmbedding(String text) throws Exception {

        // 1. Request body banao
        String requestBody = objectMapper.writeValueAsString(
                new EmbeddingRequest(EMBEDDING_MODEL, text)
        );

        // 2. HTTP request bhejo Groq API ko
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_EMBEDDING_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // 3. Response lo
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        // 4. JSON parse karo — embedding array nikalo
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode embeddingArray = root
                .path("data")
                .get(0)
                .path("embedding");

        // 5. JsonNode array ko float[] mein convert karo
        float[] embedding = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            embedding[i] = (float) embeddingArray.get(i).asDouble();
        }

        return embedding;
    }

    // Inner record for request body
    record EmbeddingRequest(String model, String input) {}
}