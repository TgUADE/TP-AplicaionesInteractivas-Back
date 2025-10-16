package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String description;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("products_count")
    private Long productsCount;
    
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
        this.productsCount = category.getProducts() != null ? 
                (long) category.getProducts().size() : 0L;
    }
}
