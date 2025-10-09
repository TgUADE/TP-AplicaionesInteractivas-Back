package com.service;

import java.util.List;
import java.util.UUID;
import com.entity.ProductImage;
import com.exceptions.ProductNotFoundException;

public interface ProductImageService {
    List<ProductImage> getImagesByProductId(UUID productId);
    ProductImage getPrimaryImageByProductId(UUID productId);
    ProductImage addImageToProduct(UUID productId, String imageUrl, String altText, Boolean isPrimary, Integer displayOrder) throws ProductNotFoundException;
    ProductImage updateImage(UUID imageId, String imageUrl, String altText, Boolean isPrimary, Integer displayOrder);
    void deleteImage(UUID imageId);
    void setPrimaryImage(UUID imageId);
}