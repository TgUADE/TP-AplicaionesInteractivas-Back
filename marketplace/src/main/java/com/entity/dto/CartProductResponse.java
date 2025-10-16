package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.CartProduct;
import com.entity.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductResponse {
    private UUID id;
    
    @JsonProperty("cart_id")
    private UUID cartId;
    
    @JsonProperty("product_id")
    private UUID productId;
    
    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("product_price")
    private Double productPrice;
    
    @JsonProperty("product_current_price")
    private Double productCurrentPrice;
    
    @JsonProperty("product_image")
    private String productImage;
    
    private Integer quantity;
    
    @JsonProperty("subtotal")
    private Double subtotal;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    public CartProductResponse(CartProduct cartProduct) {
        this.id = cartProduct.getId();
        this.cartId = cartProduct.getCart().getId();
        
        Product product = cartProduct.getProduct();
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = Math.round(product.getPrice() * 100.0) / 100.0;
        this.productCurrentPrice = Math.round(product.getCurrentPrice() * 100.0) / 100.0;
        
        // Obtener la imagen principal del producto
        this.productImage = product.getImages().stream()
                .filter(image -> image.getIsPrimary() != null && image.getIsPrimary())
                .findFirst()
                .map(image -> image.getImageUrl())
                .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl());
        
        this.quantity = cartProduct.getQuantity();
        this.subtotal = Math.round(this.productCurrentPrice * this.quantity * 100.0) / 100.0;
        this.createdAt = cartProduct.getCreatedAt();
    }
}
