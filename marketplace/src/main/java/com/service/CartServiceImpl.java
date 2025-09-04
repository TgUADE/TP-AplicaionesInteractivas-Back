package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.entity.User;
import com.entity.Cart;
import com.entity.CartProduct;
import com.entity.dto.CartRequest;
import com.exceptions.CartDuplicateException;
import com.exceptions.CartNotFoundException;
import com.repository.CartRepository;
import com.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final com.repository.ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

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
    public Cart createCart(UUID userId, CartRequest request) throws CartDuplicateException {
        User user = userService.getById(userId);
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
    public List<Cart> findByUserId(UUID userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> findByProductId(Long productId) {
        return cartRepository.findByProductsId(UUID.fromString(productId.toString()));
    }

    @Override
    @Transactional
    public Cart addProductToCart(UUID cartId, UUID productId) {
        // Método legacy - agregar con cantidad 1
        addProductToCartWithQuantity(cartId, productId, 1);
        return cartRepository.findById(cartId)
                .orElseThrow(com.exceptions.CartNotFoundException::new);
    }

    @Override
    @Transactional
    public Cart removeProductFromCart(UUID cartId, UUID productId) {
        cartProductRepository.deleteByCartIdAndProductId(cartId, productId);
        return cartRepository.findById(cartId)
                .orElseThrow(com.exceptions.CartNotFoundException::new);
    }

    @Override
    @Transactional
    public CartProduct addProductToCartWithQuantity(UUID cartId, UUID productId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(com.exceptions.CartNotFoundException::new);
        com.entity.Product product = productRepository.findById(productId)
                .orElseThrow(() -> new com.exceptions.ProductNotFoundException("Product not found"));
        
        // Verificar si el producto ya existe en el carrito
        Optional<CartProduct> existingCartProduct = cartProductRepository.findByCartIdAndProductId(cartId, productId);
        
        if (existingCartProduct.isPresent()) {
            // Si existe, sumar la cantidad
            CartProduct cartProduct = existingCartProduct.get();
            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
            return cartProductRepository.save(cartProduct);
        } else {
            // Si no existe, crear nuevo
            CartProduct cartProduct = new CartProduct(cart, product, quantity);
            return cartProductRepository.save(cartProduct);
        }
    }

    @Override
    @Transactional
    public CartProduct updateProductQuantityInCart(UUID cartId, UUID productId, Integer quantity) {
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new com.exceptions.ProductNotFoundException("Product not found in cart"));
        
        if (quantity <= 0) {
            cartProductRepository.delete(cartProduct);
            return null;
        }
        
        cartProduct.setQuantity(quantity);
        return cartProductRepository.save(cartProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartProduct> getCartProducts(UUID cartId) {
        return cartProductRepository.findByCartId(cartId);
    }
}
