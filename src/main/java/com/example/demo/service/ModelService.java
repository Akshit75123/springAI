package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ModelService {
    @Autowired
    @Qualifier("primaryChatClient")
    private ChatClient googleGenAiProChatClient;

    @Autowired
    @Qualifier("secondaryChatClient")
    private ChatClient googleGenAiChatClient;
    public ChatClient getChatClient(String provider) {

        if (provider==null || provider.isEmpty())
            return googleGenAiProChatClient; // default to primary chat client
        return switch(provider.toLowerCase()){
            case "google-genai-pro" -> {yield googleGenAiProChatClient;} // return google gen ai pro chat client
            case "google-genai" -> {yield googleGenAiChatClient;} // return azure openai chat client
            default -> throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        };
    }
}
