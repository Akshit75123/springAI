package com.example.demo.model;

public record CancelResponse(
        boolean success,
        String message,
        String orderId,
        double refundAmount
) {}

