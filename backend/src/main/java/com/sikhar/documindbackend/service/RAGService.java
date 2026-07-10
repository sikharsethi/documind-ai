package com.sikhar.documindbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikhar.documindbackend.dto.AnswerResponse;
import com.sikhar.documindbackend.dto.QuestionRequest;
import com.sikhar.documindbackend.model.DocumentChunk;
import com.sikhar.documindbackend.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RAGService {

    private final DocumentChunkRepository chunkRepository;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    public AnswerResponse askQuestion(QuestionRequest request) throws Exception {

        // 1. Find relevant chunks using keyword search
        List<DocumentChunk> relevantChunks = findRelevantChunks(
                request.getDocumentId(),
                request.getQuestion()
        );

        log.info("Found {} relevant chunks for question: {}",
                relevantChunks.size(), request.getQuestion());

        // 2. Build context from chunks
        StringBuilder context = new StringBuilder();
        for (DocumentChunk chunk : relevantChunks) {
            context.append(chunk.getContent()).append("\n\n");
        }

        // 3. If no relevant chunks found, use first 3 chunks as context
        if (relevantChunks.isEmpty()) {
            log.info("No keyword matches found, using first chunks as context");
            List<DocumentChunk> firstChunks = chunkRepository
                    .findByDocumentId(request.getDocumentId());
            int limit = Math.min(3, firstChunks.size());
            for (int i = 0; i < limit; i++) {
                context.append(firstChunks.get(i).getContent()).append("\n\n");
            }
        }

        // 4. Generate answer using Groq
        String answer = generateAnswer(
                request.getQuestion(),
                context.toString()
        );

        return new AnswerResponse(
                request.getQuestion(),
                answer,
                request.getDocumentId()
        );
    }

    private List<DocumentChunk> findRelevantChunks(
            Long documentId, String question) {

        // Extract keywords from question
        String[] words = question.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .split("\\s+");

        // Use LinkedHashSet to avoid duplicates
        Set<DocumentChunk> uniqueChunks = new LinkedHashSet<>();

        // Search for each keyword
        for (String word : words) {
            // Skip common words
            if (word.length() > 3 && !isStopWord(word)) {
                List<DocumentChunk> chunks = chunkRepository
                        .findByDocumentIdAndContentContaining(documentId, word);
                // Take top 2 chunks per keyword
                int limit = Math.min(2, chunks.size());
                for (int i = 0; i < limit; i++) {
                    uniqueChunks.add(chunks.get(i));
                }
            }
        }

        // Return max 5 chunks
        List<DocumentChunk> result = new ArrayList<>(uniqueChunks);
        return result.subList(0, Math.min(5, result.size()));
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
                "what", "where", "when", "which", "that",
                "this", "with", "from", "have", "will",
                "does", "kiya", "kya", "kaise", "batao"
        );
        return stopWords.contains(word);
    }

    private String generateAnswer(String question, String context)
            throws Exception {

        // Build prompt
        String prompt = String.format("""
            You are a helpful assistant. Answer the question based ONLY on 
            the provided context. If the answer is not in the context, 
            say "I couldn't find relevant information in the document."
            
            Context:
            %s
            
            Question: %s
            
            Answer:
            """, context, question);

        // Build request body
        String requestBody = objectMapper.writeValueAsString(
                new ChatRequest(MODEL,
                        List.of(new Message("user", prompt)),
                        1000)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        // Parse response
        JsonNode root = objectMapper.readTree(response.body());
        return root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    // Inner records for request body
    record ChatRequest(String model, List<Message> messages, int max_tokens) {}
    record Message(String role, String content) {}
}