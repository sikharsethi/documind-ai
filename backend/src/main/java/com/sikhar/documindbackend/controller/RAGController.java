package com.sikhar.documindbackend.controller;

import com.sikhar.documindbackend.dto.AnswerResponse;
import com.sikhar.documindbackend.dto.QuestionRequest;
import com.sikhar.documindbackend.model.User;
import com.sikhar.documindbackend.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RAGController {

    private final RAGService ragService;

    @PostMapping("/ask")
    public ResponseEntity<AnswerResponse> askQuestion(
            @RequestBody QuestionRequest request,
            @AuthenticationPrincipal User user) throws Exception {

        AnswerResponse response = ragService.askQuestion(request);
        return ResponseEntity.ok(response);
    }
}