package com.entity.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.entity.Cart;
import com.entity.CartProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {
    private UUID id;
    
    @JsonProperty("user_id")
    private UUID userId;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    private List<CartProductResponse> cartProducts;
    
    @JsonProperty("total_items")
    private Integer totalItems;
    
    @JsonProperty("total_amount")
    private Double totalAmount;
    
    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUser().getId();
        this.userName = cart.getUser().getName() + " " + cart.getUser().getSurname();
        this.createdAt = cart.getCreatedAt();
        
        if (cart.getCartProducts() != null) {
            this.cartProducts = cart.getCartProducts().stream()
                    .map(CartProductResponse::new)
                    .toList();
            this.totalItems = cart.getCartProducts().stream()
                    .mapToInt(CartProduct::getQuantity)
                    .sum();
            this.totalAmount = cart.getCartProducts().stream()
                    .mapToDouble(cp -> cp.getProduct().getCurrentPrice() * cp.getQuantity())
                    .sum();
        } else {
            this.cartProducts = List.of();
            this.totalItems = 0;
            this.totalAmount = 0.0;
        }
    }
}
