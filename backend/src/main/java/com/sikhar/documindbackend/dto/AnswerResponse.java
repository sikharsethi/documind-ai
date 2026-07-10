package com.sikhar.documindbackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AnswerResponse {
    private String question;
    private String answer;
    private Long documentId;
}