package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.PromotionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionRequest {
    private String name;
    private String description;
    private PromotionType type;
    private Double value;
    
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    
    @JsonProperty("product_id")
    private UUID productId;
    
    private Boolean active = true;
}