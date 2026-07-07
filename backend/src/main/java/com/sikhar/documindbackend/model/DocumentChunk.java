package com.sikhar.documindbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which document this chunk belongs to
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    // Actual text content of this chunk
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Position of chunk in document (1, 2, 3...)
    @Column(name = "chunk_index")
    private Integer chunkIndex;

    // Embedding will be added later (pgvector)
    // For now we store as text, convert later
    @Column(columnDefinition = "TEXT")
    private String embedding;
}