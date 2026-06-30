package com.forever.controller;

import com.forever.dto.ApiResponse;
import com.forever.dto.OrderRequest;
import com.forever.dto.OrderStatusRequest;
import com.forever.entity.Order;
import com.forever.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            orderService.placeOrder(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Order Placed"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/stripe")
    public ResponseEntity<?> placeOrderStripe(@RequestBody OrderRequest request,
                                              Authentication auth,
                                              HttpServletRequest httpRequest) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            String origin = httpRequest.getHeader("Origin");
            if (origin == null) {
                origin = httpRequest.getHeader("Referer");
            }
            String sessionUrl = orderService.placeOrderStripe(userId, request, origin);
            return ResponseEntity.ok(Map.of("success", true, "session_url", sessionUrl));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verifyStripe")
    public ResponseEntity<?> verifyStripe(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            Long orderId = Long.parseLong(body.get("orderId"));
            String success = body.get("success");
            boolean result = orderService.verifyStripe(userId, orderId, success);
            return ResponseEntity.ok(Map.of("success", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/list")
    public ResponseEntity<?> allOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(Map.of("success", true, "orders", orders));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/userorders")
    public ResponseEntity<?> userOrders(Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            List<Order> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(Map.of("success", true, "orders", orders));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody OrderStatusRequest request) {
        try {
            orderService.updateStatus(request.getOrderId(), request.getStatus());
            return ResponseEntity.ok(ApiResponse.success("Status Updated"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
