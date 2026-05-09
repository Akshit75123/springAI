package com.example.demo.model;

// Cancel Order Tool - Input/Output
public record CancelRequest(
        String orderId,
        String reason
) {}

