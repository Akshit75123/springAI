package com.example.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.support.RetryTemplate;

import com.google.genai.Client;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class MultiModelConfiguration {

    @Primary
    @Bean("primaryGeminiChatModel")
    public ChatModel primaryChatModel(GoogleGenAiChatModel autoConfiguredModel) {
        return autoConfiguredModel;
    }

    @Bean("secondaryGeminiChatModel")
    public ChatModel secondaryChatModel(
            @Value("${app.gemini.secondary.api-key}") String apiKey,
            @Value("${app.gemini.secondary.model}") String model,
            RetryTemplate retryTemplate) {

        Client genAiClient = Client.builder()
                .apiKey(apiKey)
                .build();

        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model(model)
                .build();

        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

        ToolExecutionEligibilityPredicate predicate = (request, response) -> true;

        return new GoogleGenAiChatModel(
                genAiClient,
                options,
                toolCallingManager,
                retryTemplate,
                ObservationRegistry.NOOP,
                predicate);
    }

    @Primary
    @Bean("primaryChatClient")
    public ChatClient primaryChatClient(
            @Qualifier("primaryGeminiChatModel") ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean("secondaryChatClient")
    public ChatClient secondaryChatClient(
            @Qualifier("secondaryGeminiChatModel") ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}