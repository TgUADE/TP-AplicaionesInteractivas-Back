package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.entity.Order;
import com.entity.dto.OrderRequest;
import com.exceptions.OrderDuplicateException;
import com.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

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
        // TODO: Implement order creation logic
        Order order = new Order();
        // Set order properties from request
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(UUID orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        
        // TODO: Update order properties from request
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        
        orderRepository.delete(order);
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        // TODO: Convert Long to UUID if needed, or change interface to use UUID
        return orderRepository.findByUserId(UUID.fromString(userId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByProductId(Long productId) {
        // TODO: Convert Long to UUID if needed, or change interface to use UUID
        return orderRepository.findByProductsId(UUID.fromString(productId.toString()));
    }
}



