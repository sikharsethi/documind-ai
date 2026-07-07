package com.sikhar.documindbackend.controller;

import com.sikhar.documindbackend.model.Document;
import com.sikhar.documindbackend.model.User;
import com.sikhar.documindbackend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {

        // @AuthenticationPrincipal automatically logged-in user inject karta hai
        Document document = documentService.uploadDocument(file, user);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getUserDocuments(
            @AuthenticationPrincipal User user) {

        List<Document> documents = documentService.getUserDocuments(user);
        return ResponseEntity.ok(documents);
    }
}