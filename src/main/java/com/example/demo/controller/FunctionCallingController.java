package com.example.demo.controller;

import com.example.demo.service.FunctionCallingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class FunctionCallingController {

    @Autowired
    private FunctionCallingService functionCallingService;

    @PostMapping("/chat/basic")
    public Map<String, Object> basicSupportChat(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "gemini") String provider,
            @RequestParam(defaultValue = "gemini-2.5-flash") String model
    ) {
        String userMessage = request.get("message");

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of(
                    "success", false,
                    "error", "Message cannot be empty"
            );
        }

        try {
            String response = functionCallingService.chatWithOrderTracking(
                    userMessage, provider, model);

            return Map.of(
                    "success", true,
                    "response", response
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Failed: " + e.getMessage()
            );
        }
    }

    @PostMapping("/chat/full")
    public Map<String, Object> fullSupportChat(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-4o") String model
    ) {
        String userMessage = request.get("message");

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of(
                    "success", false,
                    "error", "Message cannot be empty"
            );
        }

        try {
            String response = functionCallingService.chatWithFullSupport(
                    userMessage, provider, model);

            return Map.of(
                    "success", true,
                    "response", response
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Failed: " + e.getMessage()
            );
        }
    }
}