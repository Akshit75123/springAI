package com.example.demo.model;

public record RefundResponse(
        String orderId,
        String refundStatus,
        double refundAmount,
        String estimatedDate
) {}

