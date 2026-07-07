package com.sikhar.documindbackend.repository;

import com.sikhar.documindbackend.model.Document;
import com.sikhar.documindbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Get all documents for a specific user
    List<Document> findByUser(User user);
    // Check if document belongs to user
    boolean existsByIdAndUser(Long id, User user);
}