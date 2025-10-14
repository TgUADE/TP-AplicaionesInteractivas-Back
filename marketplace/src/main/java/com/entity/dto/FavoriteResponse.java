package com.entity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.entity.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    
    private UUID id;
    
    @JsonProperty("user_id")
    private UUID userId;
    
    private Product product;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("is_favorite")
    private boolean isFavorite = true;
}
