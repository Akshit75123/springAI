package com.example.demo.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ModelService;

import reactor.core.publisher.Flux;

@RestController
public class StreamController {

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

    // Flux is part of the project reactor 
    // flux is a stream of zero or more streams
    // stream of text chunks

    
    @GetMapping(value="/stream", produces="text/event-stream")
    public Flux<String> chat(
        @RequestParam(defaultValue = "openai") String provider,
        @RequestParam(required=false) String model,
        @RequestParam String message) {
            
            ChatClient chatClient = modelService.getChatClient(provider);

            if (provider.equalsIgnoreCase("gemini") && (model == null || model.isEmpty())) {
                model = "gemini-2.5-flash"; // default Gemini model if provider is Gemini and no model specified
            }
        
            if (model == null || model.isEmpty()) {
                return chatClient
                .prompt()
                .user(message)
                .options(OpenAiChatOptions
                    .builder()
                    .model(model)
                    .temperature(0.5)
                    .maxCompletionTokens(2000)
                    .build())
                .stream()
                .content();
            }
            return chatClient
                .prompt()
                .user(message)
                .options(OpenAiChatOptions
                    .builder()
                    .model(model)
                    .temperature(0.5)
                    .maxCompletionTokens(2000)
                    .build())
                .stream()
                .content();
            
    }


}
