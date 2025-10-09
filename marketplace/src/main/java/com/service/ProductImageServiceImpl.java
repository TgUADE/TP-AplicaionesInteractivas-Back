package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Product;
import com.entity.ProductImage;
import com.exceptions.ProductNotFoundException;
import com.repository.ProductImageRepository;
import com.repository.ProductRepository;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductImage> getImagesByProductId(UUID productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrder(productId);
    }

    @Override
    public ProductImage getPrimaryImageByProductId(UUID productId) {
        return productImageRepository.findPrimaryImageByProductId(productId);
    }

    @Override
    public ProductImage addImageToProduct(UUID productId, String imageUrl, String altText, Boolean isPrimary, Integer displayOrder) throws ProductNotFoundException {
        // Verificar que el producto existe
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException();
        }
        
        Product product = productOpt.get();
        
        // Si se está marcando como primaria, desmarcar las otras primarias del producto
        if (isPrimary != null && isPrimary) {
            List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
            for (ProductImage existingImage : existingImages) {
                if (existingImage.getIsPrimary()) {
                    existingImage.setIsPrimary(false);
                    productImageRepository.save(existingImage);
                }
            }
        }
        
        // Si no se especifica displayOrder, usar el siguiente número disponible
        if (displayOrder == null) {
            List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
            displayOrder = existingImages.size() + 1;
        }
        
        // Crear la nueva imagen
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(imageUrl);
        productImage.setAltText(altText);
        productImage.setIsPrimary(isPrimary != null ? isPrimary : false);
        productImage.setDisplayOrder(displayOrder);
        productImage.setProduct(product);
        
        return productImageRepository.save(productImage);
    }

    @Override
    public ProductImage updateImage(UUID imageId, String imageUrl, String altText, Boolean isPrimary, Integer displayOrder) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            return null;
        }
        
        ProductImage productImage = imageOpt.get();
        
        // Si se está marcando como primaria, desmarcar las otras primarias del mismo producto
        if (isPrimary != null && isPrimary && !productImage.getIsPrimary()) {
            List<ProductImage> existingImages = productImageRepository.findByProductId(productImage.getProduct().getId());
            for (ProductImage existingImage : existingImages) {
                if (existingImage.getIsPrimary() && !existingImage.getId().equals(imageId)) {
                    existingImage.setIsPrimary(false);
                    productImageRepository.save(existingImage);
                }
            }
        }
        
        // Actualizar los campos
        if (imageUrl != null) {
            productImage.setImageUrl(imageUrl);
        }
        if (altText != null) {
            productImage.setAltText(altText);
        }
        if (isPrimary != null) {
            productImage.setIsPrimary(isPrimary);
        }
        if (displayOrder != null) {
            productImage.setDisplayOrder(displayOrder);
        }
        
        return productImageRepository.save(productImage);
    }

    @Override
    public void deleteImage(UUID imageId) {
        productImageRepository.deleteById(imageId);
    }

    @Override
    public void setPrimaryImage(UUID imageId) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            return;
        }
        
        ProductImage productImage = imageOpt.get();
        
        // Desmarcar todas las imágenes primarias del mismo producto
        List<ProductImage> existingImages = productImageRepository.findByProductId(productImage.getProduct().getId());
        for (ProductImage existingImage : existingImages) {
            if (existingImage.getIsPrimary()) {
                existingImage.setIsPrimary(false);
                productImageRepository.save(existingImage);
            }
        }
        
        // Marcar esta imagen como primaria
        productImage.setIsPrimary(true);
        productImageRepository.save(productImage);
    }
}
