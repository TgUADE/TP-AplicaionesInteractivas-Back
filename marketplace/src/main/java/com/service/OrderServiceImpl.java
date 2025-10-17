package com.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.entity.Cart;
import com.entity.CartProduct;
import com.entity.Product;
import com.entity.User;
import com.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.entity.Order;
import com.entity.dto.OrderRequest;
import com.entity.dto.OrderProductItem;
import com.exceptions.OrderDuplicateException;
import com.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest request) throws OrderDuplicateException {
        User user = userService.getById(request.getUserId());

        Cart carrito = cartService.getCartById(request.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setCarrito(carrito);
        // Map fields from request
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            order.setStatus(request.getStatus());
        } else {
            order.setStatus("CREATED");
        }
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        // Build products JSON and total from cart products
        List<CartProduct> cartProducts = cartService.getCartProducts(request.getCartId());
        List<OrderProductItem> orderItems = cartProducts.stream()
                .map(cp -> {
                    Product product = cp.getProduct();
                    BigDecimal price = BigDecimal.valueOf(product.getPrice());
                    BigDecimal quantity = BigDecimal.valueOf(cp.getQuantity());
                    BigDecimal subtotal = price.multiply(quantity);
                    return new OrderProductItem(
                            product.getId(),
                            product.getName(),
                            price,
                            cp.getQuantity(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());
        BigDecimal total = orderItems.stream()
                .map(OrderProductItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        try {
            String productsJson = objectMapper.writeValueAsString(orderItems);
            order.setProductsJson(productsJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order items", e);
        }
        order.setTotal(total);
        order.setCreatedAt(java.time.LocalDateTime.now());

        return orderRepository.save(order);
    }


    @Override
    @Transactional
    public Order updateOrder(UUID orderId, OrderRequest request) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());

        if (request.getCartId() != null) {
            Cart cart = cartService.getCartById(request.getCartId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
            existingOrder.setCarrito(cart);
            // Recompute products JSON and total when cart changes
            List<CartProduct> cartProducts = cartService.getCartProducts(request.getCartId());
            List<OrderProductItem> orderItems = cartProducts.stream()
                    .map(cp -> {
                        Product product = cp.getProduct();
                        BigDecimal price = BigDecimal.valueOf(product.getPrice());
                        BigDecimal quantity = BigDecimal.valueOf(cp.getQuantity());
                        BigDecimal subtotal = price.multiply(quantity);
                        return new OrderProductItem(
                                product.getId(),
                                product.getName(),
                                price,
                                cp.getQuantity(),
                                subtotal
                        );
                    })
                    .collect(Collectors.toList());
            BigDecimal total = orderItems.stream()
                    .map(OrderProductItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            try {
                String productsJson = objectMapper.writeValueAsString(orderItems);
                existingOrder.setProductsJson(productsJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize order items", e);
            }
            existingOrder.setTotal(total);
        }

        if (request.getStatus() != null) {
            existingOrder.setStatus(request.getStatus());
        }

        if (request.getShippingAddress() != null) {
            existingOrder.setShippingAddress(request.getShippingAddress());
        }
        if (request.getBillingAddress() != null) {
            existingOrder.setBillingAddress(request.getBillingAddress());
        }
        if (request.getPaymentMethod() != null) {
            existingOrder.setPaymentMethod(request.getPaymentMethod());
        }

        return orderRepository.save(existingOrder);
    }


    @Override
    @Transactional
    public Order deleteOrder(UUID orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());

        orderRepository.delete(existingOrder);
        return existingOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(UUID userId) {
        return orderRepository.findByUserId(UUID.fromString(userId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByProductId(UUID productId) {
        return orderRepository.findByProductsId(UUID.fromString(productId.toString()));
    }

}
