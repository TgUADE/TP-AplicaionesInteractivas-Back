package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.entity.User;
import com.entity.Cart;
import com.entity.dto.CartRequest;
import com.exceptions.CartDuplicateException;
import com.exceptions.CartNotFoundException;
import com.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartById(UUID cartId) {
        return cartRepository.findById(cartId);
    }

    @Override
    @Transactional
    public Cart createCart(CartRequest request) throws CartDuplicateException {
        User user = userService.getById(request.getUserId());
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCreatedAt(java.time.LocalDateTime.now());
        // cart.setProducts(request.getProducts()); // Descomentar si CartRequest tiene
        // productos
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCart(UUID cartId, CartRequest request) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(CartNotFoundException::new);
        // if (request.getProducts() != null) {
        // existingCart.setProducts(request.getProducts());
        // }
        return cartRepository.save(existingCart);
    }

    @Override
    @Transactional
    public Cart deleteCart(UUID cartId) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(CartNotFoundException::new);
        cartRepository.delete(existingCart);
        return existingCart;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(UUID.fromString(userId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> findByProductId(Long productId) {
        return cartRepository.findByProductsId(UUID.fromString(productId.toString()));
    }
}
