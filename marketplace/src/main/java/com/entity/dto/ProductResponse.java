package com.entity.dto;

import java.util.List;
import java.util.UUID;

import com.entity.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    
    @JsonProperty("original_price")
    private Double originalPrice;
    
    @JsonProperty("current_price")
    private Double currentPrice;
    
    private Long stock;
    
    @JsonProperty("category_id")
    private UUID categoryId;
    
    @JsonProperty("category_name")
    private String categoryName;
    
    // Información mínima de promoción (null si no hay promoción activa)
    @JsonProperty("promotion")
    private PromotionSummary promotion;
    
    private List<ProductImage> images;
    
    // Clase interna para información resumida de promoción
    @Data
    public static class PromotionSummary {
        private UUID id;
        private String name;
        private String type;
        private Double value;
        
        @JsonProperty("end_date")
        private String endDate;
    }
}