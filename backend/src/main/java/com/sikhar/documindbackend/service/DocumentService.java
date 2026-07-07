package com.sikhar.documindbackend.service;

import com.sikhar.documindbackend.model.Document;
import com.sikhar.documindbackend.model.DocumentChunk;
import com.sikhar.documindbackend.model.User;
import com.sikhar.documindbackend.repository.DocumentChunkRepository;
import com.sikhar.documindbackend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;

    // Maximum words per chunk
    private static final int CHUNK_SIZE = 500;

    public Document uploadDocument(MultipartFile file, User user)
            throws IOException {

        // 1. Extract text from PDF using PDFBox
        String extractedText = extractTextFromPdf(file);

        // 2. Split text into chunks
        List<String> chunks = splitIntoChunks(extractedText, CHUNK_SIZE);

        // 3. Save document metadata
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setOriginalName(file.getOriginalFilename());
        document.setUser(user);
        document.setChunkCount(chunks.size());
        Document savedDocument = documentRepository.save(document);

        // 4. Save each chunk
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocument(savedDocument);
            chunk.setContent(chunks.get(i));
            chunk.setChunkIndex(i);
            chunkRepository.save(chunk);
        }

        return savedDocument;
    }

    private String extractTextFromPdf(MultipartFile file)
            throws IOException {
        // PDFBox 3.x mein Loader.loadPDF() uses
        PDDocument pdDocument = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(pdDocument);
        pdDocument.close();
        return text;
    }

    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        // Split by words
        String[] words = text.split("\\s+");
        StringBuilder chunk = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            chunk.append(word).append(" ");
            wordCount++;

            // When chunk is full, save it and start new one
            if (wordCount >= chunkSize) {
                chunks.add(chunk.toString().trim());
                chunk = new StringBuilder();
                wordCount = 0;
            }
        }

        // Add remaining words as last chunk
        if (chunk.length() > 0) {
            chunks.add(chunk.toString().trim());
        }

        return chunks;
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }
}