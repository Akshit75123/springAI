package com.example.demo.controller;

import com.example.demo.model.SuggestedResponse;
import com.example.demo.model.TicketAnalysis;
import com.example.demo.service.TicketAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketAnalysisController {

    @Autowired
    private TicketAnalysisService ticketAnalysisService;

    @PostMapping("/analyze")
    public Map<String, Object> analyzeTicket(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-5") String model
    ) {
        String ticketText = request.get("ticketText");

        try {
            TicketAnalysis analysis = ticketAnalysisService.analyzeTicket(ticketText, provider, model);

            System.out.println(analysis.getPriority());

            if (analysis.getPriority().equals(TicketAnalysis.Priority.CRITICAL)
                    && analysis.getSentiment().equals(TicketAnalysis.Sentiment.ANGRY)) {

                List<SuggestedResponse> responses = ticketAnalysisService
                        .generateUrgentResponses(analysis, provider, model);

                return Map.of(
                        "success", true,
                        "analysis", analysis,
                        "responses", responses,
                        "priorityColor", ticketAnalysisService.getPriorityColor(analysis.getPriority()),
                        "sentimentEmoji", ticketAnalysisService.getSentimentEmoji(analysis.getSentiment())
                );
            }

            return Map.of(
                    "success", true,
                    "analysis", analysis,
                    "priorityColor", ticketAnalysisService.getPriorityColor(analysis.getPriority()),
                    "sentimentEmoji", ticketAnalysisService.getSentimentEmoji(analysis.getSentiment())
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Failed to analyze ticket: " + e.getMessage()
            );
        }
    }
}
