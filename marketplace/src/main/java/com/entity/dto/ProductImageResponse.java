package com.entity.dto;

import java.util.UUID;
import lombok.Data;
import com.entity.ProductImage;

@Data
public class ProductImageResponse {
    private UUID id;
    private String imageUrl;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;
    private UUID productId;
    
    public ProductImageResponse() {}
    
    public ProductImageResponse(ProductImage productImage) {
        this.id = productImage.getId();
        this.imageUrl = productImage.getImageUrl();
        this.altText = productImage.getAltText();
        this.isPrimary = productImage.getIsPrimary();
        this.displayOrder = productImage.getDisplayOrder();
        this.productId = productImage.getProduct().getId();
    }
}