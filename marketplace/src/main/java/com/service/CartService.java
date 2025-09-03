package com.service;

import com.entity.Cart;
import com.entity.dto.CartRequest;
import com.exceptions.CartDuplicateException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
    List<Cart> getCarts();

    Optional<Cart> getCartById(UUID cartId);

    Cart createCart(UUID userId, CartRequest request) throws CartDuplicateException;

    Cart updateCart(UUID cartId, CartRequest request);

    Cart deleteCart(UUID cartId);

    List<Cart> findByUserId(UUID userId);

    List<Cart> findByProductId(Long productId);

    Cart addProductToCart(UUID cartId, UUID productId);

    Cart removeProductFromCart(UUID cartId, UUID productId);
}
