package com.sikhar.documindbackend.repository;

import com.sikhar.documindbackend.model.DocumentChunk;
import com.sikhar.documindbackend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentOrderByChunkIndex(Document document);

    void deleteByDocument(Document document);

    // Keyword search — find chunks containing the question words
    @Query("SELECT dc FROM DocumentChunk dc WHERE dc.document.id = :documentId " +
            "AND LOWER(dc.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY dc.chunkIndex")
    List<DocumentChunk> findByDocumentIdAndContentContaining(
            @Param("documentId") Long documentId,
            @Param("keyword") String keyword
    );

    // Get all chunks for a document (for context)
    @Query("SELECT dc FROM DocumentChunk dc WHERE dc.document.id = :documentId " +
            "ORDER BY dc.chunkIndex")
    List<DocumentChunk> findByDocumentId(@Param("documentId") Long documentId);
}