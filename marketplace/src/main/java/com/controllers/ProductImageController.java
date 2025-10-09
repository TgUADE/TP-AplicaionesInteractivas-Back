package com.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.entity.ProductImage;
import com.entity.dto.ProductImageRequest;
import com.entity.dto.ProductImageResponse;
import com.exceptions.ProductNotFoundException;
import com.service.ProductImageService;

@RestController
@RequestMapping("products/{productId}/images")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable UUID productId) {
        List<ProductImage> images = productImageService.getImagesByProductId(productId);
        List<ProductImageResponse> response = images.stream()
            .map(ProductImageResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/primary")
    public ResponseEntity<ProductImageResponse> getPrimaryImage(@PathVariable UUID productId) {
        ProductImage primaryImage = productImageService.getPrimaryImageByProductId(productId);
        return ResponseEntity.ok(new ProductImageResponse(primaryImage));
    }

    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(@PathVariable UUID productId, @RequestBody ProductImageRequest request) throws ProductNotFoundException {
        ProductImage image = productImageService.addImageToProduct(
            productId, 
            request.getImageUrl(), 
            request.getAltText(), 
            request.getIsPrimary(), 
            request.getDisplayOrder()
        );
        return ResponseEntity.ok(new ProductImageResponse(image));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> updateImage(@PathVariable UUID imageId, @RequestBody ProductImageRequest request) {
        ProductImage image = productImageService.updateImage(
            imageId, 
            request.getImageUrl(), 
            request.getAltText(), 
            request.getIsPrimary(), 
            request.getDisplayOrder()
        );
        return ResponseEntity.ok(new ProductImageResponse(image));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<Void> setPrimaryImage(@PathVariable UUID imageId) {
        productImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok().build();
    }
}