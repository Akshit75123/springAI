package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
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
    public String chat(@RequestHeader(value="AI-Provider",defaultValue = "openai") String provider,
        @RequestHeader(value="AI-Model",required=false) String model,
        @RequestBody String message) {
        
            
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
                .call()
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
                .call()
                .content();
            
    }


}