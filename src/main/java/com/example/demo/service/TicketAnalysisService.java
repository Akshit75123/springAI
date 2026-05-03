package com.example.demo.service;

import com.example.demo.model.SuggestedResponse;
import com.example.demo.model.TicketAnalysis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class TicketAnalysisService {

    private final ModelService modelService;
    private final ResourceLoader resourceLoader;

    public TicketAnalysisService(ModelService modelService, ResourceLoader resourceLoader) {
        this.modelService = modelService;
        this.resourceLoader = resourceLoader;
    }

    public TicketAnalysis analyzeTicket(String ticketText, String provider, String model) {
        ChatClient chatClient = modelService.getChatClient(provider);
        Prompt prompt = createTicketAnalysisPrompt(ticketText);

        return chatClient
                .prompt(prompt)
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .call()
                .entity(TicketAnalysis.class);
    }

    public List<SuggestedResponse> generateUrgentResponses(TicketAnalysis analysis, String provider, String model) {
        ChatClient chatClient = modelService.getChatClient(provider);
        Prompt prompt = createTicketAnalysisResponsesPrompt(analysis);

        return chatClient
                .prompt(prompt)
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .call()
                .entity(new ParameterizedTypeReference<List<SuggestedResponse>>() {});
    }

    private Prompt createTicketAnalysisPrompt(String ticketText) {
        String templateContent = loadTemplate("classpath:templates/ticket-analysis.txt");
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);
        Map<String, Object> variables = Map.of("ticketText", ticketText);
        return promptTemplate.create(variables);
    }

    private Prompt createTicketAnalysisResponsesPrompt(TicketAnalysis analysis) {
        String templateContent = loadTemplate("classpath:templates/ticket-analysis-responses.txt");
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);
        Map<String, Object> variables = Map.of(
                "category", analysis.getCategory(),
                "issues", analysis.getKeyIssues()
        );
        return promptTemplate.create(variables);
    }

    private String loadTemplate(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ticket analysis template", e);
        }
    }

    public String getPriorityColor(TicketAnalysis.Priority priority) {
        return switch (priority) {
            case CRITICAL -> "#dc3545";
            case HIGH -> "#fd7e14";
            case MEDIUM -> "#ffc107";
            case LOW -> "#28a745";
        };
    }

    public String getSentimentEmoji(TicketAnalysis.Sentiment sentiment) {
        return switch (sentiment) {
            case HAPPY -> "😊";
            case NEUTRAL -> "😐";
            case FRUSTRATED -> "😤";
            case ANGRY -> "😡";
        };
    }
}
