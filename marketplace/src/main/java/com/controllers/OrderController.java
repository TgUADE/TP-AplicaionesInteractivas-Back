package com.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Order;
import com.entity.dto.OrderRequest;
import com.exceptions.OrderNotFoundException;
import com.service.OrderService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) throws OrderNotFoundException {
        Order result = orderService.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());
        return ResponseEntity.ok(result);
    }

    // Obtener todas las órdenes de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Obtener todas las órdenes que contienen un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Order>> getOrdersByProductId(@PathVariable Long productId) {
        List<Order> orders = orderService.findByProductId(productId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest) {
        Order result = orderService.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/orders/" + result.getId())).body(result);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable UUID orderId, @RequestBody OrderRequest orderRequest)
            throws OrderNotFoundException {
        Order result = orderService.updateOrder(orderId, orderRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable UUID orderId)
            throws OrderNotFoundException {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
