package com.sikhar.documindbackend.dto;

import lombok.Data;

@Data
public class QuestionRequest {
    private Long documentId;
    private String question;
}