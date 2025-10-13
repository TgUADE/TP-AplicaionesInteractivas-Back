package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.PromotionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionSummary {
    private UUID id;
    private String name;
    private PromotionType type;
    private Double value;
    
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    
    private Boolean active;
    
    @JsonProperty("is_valid")
    private Boolean isValid;
    
    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("days_remaining")
    private Long daysRemaining;
}