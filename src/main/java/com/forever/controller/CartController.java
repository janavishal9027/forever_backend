package com.forever.controller;

import com.forever.dto.ApiResponse;
import com.forever.dto.CartItemResponse;
import com.forever.dto.CartRequest;
import com.forever.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request, Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            cartService.addToCart(userId, request.getItemId(), request.getSize());
            return ResponseEntity.ok(ApiResponse.success("Added to Cart"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody CartRequest request, Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            cartService.updateCart(userId, request.getItemId(), request.getSize(), request.getQuantity());
            return ResponseEntity.ok(ApiResponse.success("Cart Updated"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/get")
    public ResponseEntity<?> getUserCart(Authentication auth) {
        try {
            Long userId = Long.parseLong(auth.getPrincipal().toString());
            List<CartItemResponse> cartData = cartService.getUserCart(userId);
            return ResponseEntity.ok(Map.of("success", true, "cartData", cartData));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
