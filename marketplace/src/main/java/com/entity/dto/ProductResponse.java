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
    
    // Métodos setter personalizados para redondear precios a 2 decimales
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice != null ? Math.round(originalPrice * 100.0) / 100.0 : null;
    }
    
    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice != null ? Math.round(currentPrice * 100.0) / 100.0 : null;
    }
    
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
    public static class PromotionSummary {
        private UUID id;
        private String name;
        private String type;
        private Double value;
        
        @JsonProperty("end_date")
        private String endDate;
        
        // Getters y setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Double getValue() { return value; }
        public void setValue(Double value) {
            this.value = value != null ? Math.round(value * 100.0) / 100.0 : null;
        }
        
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }
}