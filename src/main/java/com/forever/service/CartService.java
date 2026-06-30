package com.forever.service;

import com.forever.dto.CartItemResponse;
import com.forever.entity.CartItem;
import com.forever.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    public void addToCart(Long userId, Long productId, String size) {
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductIdAndSize(userId, productId, size);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setSize(size);
            item.setQuantity(1);
            cartItemRepository.save(item);
        }
    }

    public void updateCart(Long userId, Long productId, String size, Integer quantity) {
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductIdAndSize(userId, productId, size);

        if (existing.isPresent()) {
            if (quantity <= 0) {
                cartItemRepository.delete(existing.get());
            } else {
                CartItem item = existing.get();
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }
    }

    public List<CartItemResponse> getUserCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(item -> new CartItemResponse(item.getProductId(), item.getSize(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
