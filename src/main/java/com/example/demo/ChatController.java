package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ModelService;


@RestController
@RequestMapping("/api")
public class ChatController {
    // This is a placeholder for the ChatController class.
    // You can implement your chat-related logic here.
    
    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ModelService modelService;

    // public ChatController(ChatModel chatModel) {
    //     this.chatClient = ChatClient.builder(
    //         chatModel).build();
    // }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping("/chat")
    public String chat(@RequestHeader(value="AI-Provider",defaultValue = "google-genai-pro") String provider,
        @RequestHeader(value="AI-Model",defaultValue = "gemini-3-flash-preview") String model,
        @RequestBody String message) {
        
            ChatClient chatClient = modelService.getChatClient(provider);
        
            if (model == null || model.isEmpty()) {
                return chatClient
                .prompt()
                .user(message)
                .options(GoogleGenAiChatOptions
                    .builder()
                    .model(model)
                    .temperature(1.5)
                    .maxOutputTokens(2000)
                    .build())
                .call()
                .content();
            }
            return chatClient
                .prompt()
                .user(message)
                .options(GoogleGenAiChatOptions
                    .builder()
                    .temperature(1.5)
                    .maxOutputTokens(2000)
                    .build())
                .call()
                .content();
            
    }


}