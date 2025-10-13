package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.entity.Product;
import com.entity.dto.ProductResponse;
import com.exceptions.CategoryNotFoundException;
import com.exceptions.ProductDuplicateException;
import com.exceptions.ProductNotFoundException;

public interface ProductService {
    List<ProductResponse> getProducts();
    Optional<Product> getProductById(UUID productId);
    Product createProduct(String name, String description, double price, long stock, UUID categoryId) throws ProductDuplicateException, CategoryNotFoundException;
    Product deleteProduct(UUID productId);
    Product updateProduct(UUID productId, String name, String description, Double price, Long stock, UUID categoryId) throws ProductNotFoundException, CategoryNotFoundException;
    List<ProductResponse> getProductsByCategory(UUID categoryId) throws CategoryNotFoundException;
    ProductResponse toProductResponse(Product product);
    List<ProductResponse> getProductsOnSale();
}