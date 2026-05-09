package com.example.demo.service;

import com.example.demo.config.FunctionConfig;
import com.example.demo.service.ModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionCallingService {

    @Autowired
    private ModelService modelService;

    @Autowired
    private FunctionConfig orderSupportTools;

    public String chatWithOrderTracking(
            String userMessage,
            String provider,
            String model) {

        ChatClient chatClient = modelService.getChatClient(provider);

        return chatClient
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .user(userMessage)
                .tools(orderSupportTools)
                .call()
                .content();
    }

    public String chatWithFullSupport(
            String userMessage,
            String provider,
            String model) {

        ChatClient chatClient = modelService.getChatClient(provider);

        return chatClient
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .system("""
                You are a helpful customer support assistant for an e-commerce company.
                
                You have access to these tools:
                - getOrderStatus: Check order status and tracking
                - cancelOrder: Cancel orders that haven't shipped yet
                - initiateReturn: Start return process for delivered orders
                - checkRefund: Check refund status for cancelled orders
                
                Use these tools to help customers. Be friendly and helpful.
                Always confirm actions before executing them.
                If a tool returns an error, explain it clearly to the customer.
                """)
                .user(userMessage)
                .tools(orderSupportTools)
                .call()
                .content();
    }
}