package com.entity.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private long stock;
    
    @JsonProperty("category_id")
    private UUID categoryId;
}
