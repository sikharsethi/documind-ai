package com.sikhar.documindbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
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

    // Vector embedding — stores meaning of text as numbers
    // 1536 dimensions for OpenAI, 768 for most other models
    @Column(columnDefinition = "vector(768)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] embedding;
}