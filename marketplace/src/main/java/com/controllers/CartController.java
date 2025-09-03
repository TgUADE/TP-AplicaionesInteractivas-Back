package com.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Cart;
import com.entity.dto.CartRequest;
import com.exceptions.CartNotFoundException;
import com.service.CartService;

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
@RequestMapping("carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<Cart>> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable UUID cartId) throws CartNotFoundException {
        Cart result = cartService.getCartById(cartId)
                .orElseThrow(CartNotFoundException::new);
        return ResponseEntity.ok(result);
    }

    // Obtener todos los carritos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getCartsByUserId(@PathVariable Long userId) {
        List<Cart> carts = cartService.findByUserId(userId);
        return ResponseEntity.ok(carts);
    }

    // Obtener todos los carritos que contienen un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Cart>> getCartsByProductId(@PathVariable Long productId) {
        List<Cart> carts = cartService.findByProductId(productId);
        return ResponseEntity.ok(carts);
    }

    @PostMapping
    public ResponseEntity<Object> createCart(@RequestBody CartRequest cartRequest) {
        Cart result = cartService.createCart(cartRequest);
        return ResponseEntity.created(URI.create("/carts/" + result.getId())).body(result);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> updateCart(@PathVariable UUID cartId, @RequestBody CartRequest cartRequest)
            throws CartNotFoundException {
        Cart result = cartService.updateCart(cartId, cartRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Cart> deleteCart(@PathVariable UUID cartId)
            throws CartNotFoundException {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
