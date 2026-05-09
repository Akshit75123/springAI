package com.example.demo.model;

public record ReturnResponse(
        boolean success,
        String message,
        String returnId,
        String returnLabel
) {}
