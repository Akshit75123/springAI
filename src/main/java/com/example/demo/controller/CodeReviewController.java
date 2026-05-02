package com.example.demo.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.CodeReviewService;
import com.example.demo.service.ModelService;

import java.util.Map;

@RestController
@RequestMapping("/api/code-review")
public class CodeReviewController {

    @Autowired
    private ModelService modelService;

    @Autowired
    private CodeReviewService codeReviewService;

    @PostMapping
    public Map<String, Object> reviewCode(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-5.4-mini") String model
    ) {
        String code = request.get("code");
        String language = request.getOrDefault("language", "Java");
        String businessRequirements = request.get("businessRequirements");

        if (code == null || code.trim().isEmpty()) {
            return Map.of(
                    "error", true,
                    "message", "Code cannot be empty"
            );
        }

        if (!"openai".equalsIgnoreCase(provider) &&
                (model == null || model.isEmpty())) {

            return Map.of(
                    "error", true,
                    "message", "AI-Model header is required when using provider: " + provider
            );
        }

        Prompt prompt = codeReviewService.createCodeReviewPrompt(code, language, businessRequirements);

        ChatClient chatClient = modelService.getChatClient(provider);

        String review = chatClient.prompt()
                .user(prompt.getContents())
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(0.2)
                        // .maxCompletionTokens(2000)
                        .build())
                .call()
                .content();

        return Map.of(
                "success", true,
                "language", language,
                "provider", provider,
                "model", model,
                "review", review,
                "codeLength", code.length(),
                "hasBusinessRequirements", businessRequirements != null && !businessRequirements.trim().isEmpty()
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "healthy",
                "service", "Code Review Service"
        );
    }
}
