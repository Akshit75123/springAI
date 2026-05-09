package com.example.demo.config;


import com.example.demo.model.CancelResponse;
import com.example.demo.model.OrderResponse;
import com.example.demo.model.RefundResponse;
import com.example.demo.model.ReturnResponse;
import com.example.demo.service.OrderService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionConfig {

    @Autowired
    private OrderService orderService;

    @Tool(description = "Get order status, tracking info, and delivery estimate by order ID")
    public OrderResponse getOrderStatus(
            @ToolParam(description = "The order ID to check")
            String orderId
    ) {
        System.out.println("🔧 AI used tool: getOrderStatus(" + orderId + ")");
        return orderService.getOrderStatus(orderId);
    }

    @Tool(description = "Cancel an order that hasn't shipped yet. Returns refund info if successful.")
    public CancelResponse cancelOrder(
            @ToolParam(description = "The order ID to cancel")
            String orderId,
            @ToolParam(description = "Reason for cancellation")
            String reason
    ) {
        System.out.println("🔧 AI used tool: cancelOrder(" + orderId + ", reason: " + reason + ")");
        return orderService.cancelOrder(orderId, reason);
    }

    @Tool(description = "Start return process for a delivered order. Customer must provide reason for return.")
    public ReturnResponse initiateReturn(
            @ToolParam(description = "The order ID to return")
            String orderId,
            @ToolParam(description = "Reason for return")
            String reason
    ) {
        System.out.println("🔧 AI used tool: initiateReturn(" + orderId + ", reason: " + reason + ")");
        return orderService.initiateReturn(orderId, reason);
    }

    @Tool(description = "Check the status of a refund for a cancelled order")
    public RefundResponse checkRefund(
            @ToolParam(description = "The order ID to check refund status for")
            String orderId
    ) {
        System.out.println("🔧 AI used tool: checkRefund(" + orderId + ")");
        return orderService.checkRefund(orderId);
    }
}