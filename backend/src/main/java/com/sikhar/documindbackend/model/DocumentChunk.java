package com.sikhar.documindbackend.model;

import com.sikhar.documindbackend.model.Document;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    // Embedding removed — using keyword search for RAG
}