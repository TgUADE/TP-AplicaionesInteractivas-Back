package com.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.entity.Order;
import com.entity.dto.OrderProductItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {
    private UUID id;
    
    @JsonProperty("user_id")
    private UUID userId;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("user_email")
    private String userEmail;
    
    @JsonProperty("cart_id")
    private UUID cartId;
    
    private String status;
    
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("billing_address")
    private String billingAddress;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("is_paid")
    private Boolean isPaid;
    
    private BigDecimal total;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    private List<OrderProductItem> products;
    
    @JsonProperty("total_items")
    private Integer totalItems;
    
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.userName = order.getUser().getName() + " " + order.getUser().getSurname();
        this.userEmail = order.getUser().getEmail();
        this.cartId = order.getCarrito() != null ? order.getCarrito().getId() : null;
        this.status = order.getStatus();
        this.shippingAddress = order.getShippingAddress();
        this.billingAddress = order.getBillingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.isPaid = order.getIsPaid();
        this.total = order.getTotal();
        this.createdAt = order.getCreatedAt();
        
        // Parsear el JSON de productos
        if (order.getProductsJson() != null && !order.getProductsJson().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.products = mapper.readValue(order.getProductsJson(), new TypeReference<List<OrderProductItem>>() {});
                this.totalItems = this.products.stream()
                        .mapToInt(OrderProductItem::getQuantity)
                        .sum();
            } catch (Exception e) {
                this.products = List.of();
                this.totalItems = 0;
            }
        } else {
            this.products = List.of();
            this.totalItems = 0;
        }
    }
}
