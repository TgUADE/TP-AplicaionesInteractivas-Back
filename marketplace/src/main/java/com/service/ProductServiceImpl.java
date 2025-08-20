package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Category;
import com.entity.Product;
import com.exceptions.CategoryNotFoundException;
import com.exceptions.ProductDuplicateException;
import com.exceptions.ProductNotFoundException;
import com.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(UUID productId) {
        return productRepository.findById(productId);
    }


    public Product createProduct(String name, String description, double price, long stock, UUID categoryId) throws ProductDuplicateException, CategoryNotFoundException {
        List<Product> products = productRepository.findAll();
        if (products.stream().anyMatch(product -> product.getDescription().equals(description))) {
            throw new ProductDuplicateException();
        }
        
        Category category = categoryService.getCategoryById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException());
        
        return productRepository.save(new Product(name, description, price, stock, category));
    }

    public Product deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        productRepository.deleteById(productId);
        return product;
    }

    public Product updateProduct(UUID productId, String name, String description, double price, long stock, UUID categoryId) throws ProductNotFoundException, CategoryNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        
        Category category = categoryService.getCategoryById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException());
        
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public List<Product> getProductsByCategory(UUID categoryId) throws CategoryNotFoundException {
        categoryService.getCategoryById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException());
        
        return productRepository.findAll().stream()
            .filter(product -> product.getCategory() != null && 
                    product.getCategory().getId().equals(categoryId))
            .collect(java.util.stream.Collectors.toList());
    }

}
