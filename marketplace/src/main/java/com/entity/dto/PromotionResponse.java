package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.Product;
import com.entity.PromotionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionResponse {
    private UUID id;
    private String name;
    private String description;
    private PromotionType type;
    private Double value;
    
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    
    private Boolean active;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("original_price")
    private Double originalPrice;
    
    @JsonProperty("discounted_price")
    private Double discountedPrice;
    
    @JsonProperty("discount_amount")
    private Double discountAmount;
    
    @JsonProperty("is_valid")
    private Boolean isValid;
}