package com.forever.service;

import com.forever.dto.OrderRequest;
import com.forever.entity.Order;
import com.forever.entity.OrderItem;
import com.forever.repository.OrderRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    private static final double DELIVERY_CHARGE = 10.0;

    @Transactional
    public void placeOrder(Long userId, OrderRequest request) {
        Order order = createOrder(userId, request, "COD");
        order.setPayment(false);
        orderRepository.save(order);
        cartService.clearCart(userId);
    }

    @Transactional
    public String placeOrderStripe(Long userId, OrderRequest request, String origin) {
        Order order = createOrder(userId, request, "Stripe");
        order.setPayment(false);
        Order savedOrder = orderRepository.save(order);

        try {
            List<SessionCreateParams.LineItem> lineItems = request.getItems().stream()
                    .map(item -> SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getName())
                                            .build())
                                    .setUnitAmount((long) (item.getPrice() * 100))
                                    .build())
                            .setQuantity(item.getQuantity().longValue())
                            .build())
                    .collect(Collectors.toList());

            // Add delivery charge
            lineItems.add(SessionCreateParams.LineItem.builder()
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Delivery Charges")
                                    .build())
                            .setUnitAmount((long) (DELIVERY_CHARGE * 100))
                            .build())
                    .setQuantity(1L)
                    .build());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(origin + "/verify?success=true&orderId=" + savedOrder.getId())
                    .setCancelUrl(origin + "/verify?success=false&orderId=" + savedOrder.getId())
                    .addAllLineItem(lineItems)
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe payment failed: " + e.getMessage());
        }
    }

    @Transactional
    public boolean verifyStripe(Long userId, Long orderId, String success) {
        if ("true".equals(success)) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setPayment(true);
            orderRepository.save(order);
            cartService.clearCart(userId);
            return true;
        } else {
            orderRepository.deleteById(orderId);
            return false;
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByDateDesc();
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    private Order createOrder(Long userId, OrderRequest request, String paymentMethod) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(request.getAmount());
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Order Placed");
        order.setDate(System.currentTimeMillis());

        // Set address fields
        OrderRequest.AddressDto address = request.getAddress();
        order.setFirstName(address.getFirstName());
        order.setLastName(address.getLastName());
        order.setEmail(address.getEmail());
        order.setStreet(address.getStreet());
        order.setCity(address.getCity());
        order.setState(address.getState());
        order.setZipcode(address.getZipcode());
        order.setCountry(address.getCountry());
        order.setPhone(address.getPhone());

        // Set order items
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemDto -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(itemDto.getProductId());
                    orderItem.setName(itemDto.getName());
                    orderItem.setPrice(itemDto.getPrice());
                    orderItem.setSize(itemDto.getSize());
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setImage(itemDto.getImage());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        return order;
    }
}
