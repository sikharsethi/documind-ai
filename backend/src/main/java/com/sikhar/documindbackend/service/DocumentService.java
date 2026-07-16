package com.sikhar.documindbackend.service;

import com.sikhar.documindbackend.model.Document;
import com.sikhar.documindbackend.model.DocumentChunk;
import com.sikhar.documindbackend.model.User;
import com.sikhar.documindbackend.repository.DocumentChunkRepository;
import com.sikhar.documindbackend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    private static final int CHUNK_SIZE = 500;

    public Document uploadDocument(MultipartFile file, User user)
            throws IOException {

        // 1. Extract text from PDF
        String extractedText = extractTextFromPdf(file);
        log.info("Extracted {} characters from PDF", extractedText.length());

        // 2. Split into chunks
        List<String> chunks = splitIntoChunks(extractedText, CHUNK_SIZE);
        log.info("Created {} chunks", chunks.size());

        // 3. Save document metadata
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setOriginalName(file.getOriginalFilename());
        document.setUser(user);
        document.setChunkCount(chunks.size());
        Document savedDocument = documentRepository.save(document);

        // 4. Save each chunk with its embedding
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocument(savedDocument);
            chunk.setContent(chunkText);
            chunk.setChunkIndex(i);

            // Generate embedding for this chunk
            try {
                // Skipping embedding — using keyword search for RAG
                log.info("Saving chunk {}/{}", i + 1, chunks.size());
            } catch (Exception e) {
                log.error("Failed for chunk {}: {}", i, e.getMessage());
            }

            chunkRepository.save(chunk);
        }

        return savedDocument;
    }

    private String extractTextFromPdf(MultipartFile file)
            throws IOException {
        PDDocument pdDocument = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(pdDocument);
        pdDocument.close();
        return text;
    }

    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder chunk = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            chunk.append(word).append(" ");
            wordCount++;

            if (wordCount >= chunkSize) {
                chunks.add(chunk.toString().trim());
                chunk = new StringBuilder();
                wordCount = 0;
            }
        }

        if (chunk.length() > 0) {
            chunks.add(chunk.toString().trim());
        }

        return chunks;
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }
}