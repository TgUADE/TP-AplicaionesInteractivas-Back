package com.entity.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;  // Cambio de double a Double para permitir null
    private Long stock;    // Cambio de long a Long para permitir null
    
    @JsonProperty("category_id")
    private UUID categoryId;
}
