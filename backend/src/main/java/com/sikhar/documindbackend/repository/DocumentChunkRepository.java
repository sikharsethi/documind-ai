package com.sikhar.documindbackend.repository;

import com.sikhar.documindbackend.model.DocumentChunk;
import com.sikhar.documindbackend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {
    // Get all chunks for a document in order
    List<DocumentChunk> findByDocumentOrderByChunkIndex(Document document);
    // Delete all chunks when document is deleted
    void deleteByDocument(Document document);
}