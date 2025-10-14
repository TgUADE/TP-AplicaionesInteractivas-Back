package com.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Category;
import com.entity.Product;
import com.entity.Promotion;
import com.entity.dto.ProductResponse;
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

    public List<ProductResponse> getProducts() {
        
        
        // 1. Cargar productos con categorías (1 consulta)
        List<Product> allProducts = productRepository.findAllWithCategories();
        
        if (!allProducts.isEmpty()) {
            // 2. Cargar promociones para todos los productos (1 consulta)
            productRepository.findPromotionsForProducts(allProducts);
            
            // 3. Cargar imágenes para todos los productos (1 consulta)
            productRepository.findImagesForProducts(allProducts);
        }
        
        System.out.println("getProducts 3 - Loaded " + allProducts.size() + " products with 3 SQL queries total");
        return toProductResponseList(allProducts);
    }

    public Optional<Product> getProductById(UUID productId) {
        return productRepository.findById(productId);
    }


    public Product createProduct(String name, String description, double price, long stock, UUID categoryId) throws ProductDuplicateException, CategoryNotFoundException {
        List<Product> products = productRepository.findAll();
        if (products.stream().anyMatch(product -> 
            Objects.equals(product.getName(), name))) {
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

    public Product updateProduct(UUID productId, String name, String description, Double price, Long stock, UUID categoryId) throws ProductNotFoundException, CategoryNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        
        // Solo actualizar campos que no sean null
        if (name != null) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (stock != null) {
            product.setStock(stock);
        }
        if (categoryId != null && !categoryId.equals(product.getCategory().getId())) {
            Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());
            product.setCategory(category);
        }
        
        return productRepository.save(product);
    }

    public List<ProductResponse> getProductsByCategory(UUID categoryId) throws CategoryNotFoundException {
        categoryService.getCategoryById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException());
        
        // 1. Cargar productos con categorías (1 consulta)
        List<Product> allProducts = productRepository.findAllWithCategories();
        
        // Filtrar por categoría
        List<Product> products = allProducts.stream()
            .filter(product -> product.getCategory() != null && 
                    product.getCategory().getId().equals(categoryId))
            .collect(java.util.stream.Collectors.toList());
        
        if (!products.isEmpty()) {
            // 2. Cargar promociones para productos de esta categoría (1 consulta)
            productRepository.findPromotionsForProducts(products);
            
            // 3. Cargar imágenes para productos de esta categoría (1 consulta)
            productRepository.findImagesForProducts(products);
        }
            
        return toProductResponseList(products);
    }

    /**
     * Convierte una entidad Product a ProductResponse incluyendo información de promociones
     */
    @Override
    public ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setOriginalPrice(product.getPrice());
        response.setCurrentPrice(product.getCurrentPrice());
        response.setStock(product.getStock());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getDescription());
        
        // Asignar imágenes del producto
        response.setImages(product.getImages());
        
        // Solo incluir información de promoción si existe (más eficiente)
        if (product.hasActivePromotion()) {
            Promotion activePromotion = product.getActivePromotion().get();
            
            ProductResponse.PromotionSummary promotionSummary = new ProductResponse.PromotionSummary();
            promotionSummary.setId(activePromotion.getId());
            promotionSummary.setName(activePromotion.getName());
            promotionSummary.setType(activePromotion.getType().toString());
            promotionSummary.setValue(activePromotion.getValue());
            promotionSummary.setEndDate(activePromotion.getEndDate().toString());
            
            response.setPromotion(promotionSummary);
        } else {
            response.setPromotion(null);
        }
        
        return response;
    }

    /**
     * Convierte una lista de Product a ProductResponse
     */
    public List<ProductResponse> toProductResponseList(List<Product> products) {
        return products.stream()
                .map(this::toProductResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene solo los productos que tienen promoción activa
     */
    @Override
    public List<ProductResponse> getProductsOnSale() {
        // 1. Cargar productos con categorías (1 consulta)
        List<Product> allProducts = productRepository.findAllWithCategories();
        
        if (!allProducts.isEmpty()) {
            // 2. Cargar promociones para todos los productos (1 consulta)
            productRepository.findPromotionsForProducts(allProducts);
            
            // 3. Cargar imágenes para todos los productos (1 consulta)
            productRepository.findImagesForProducts(allProducts);
        }
        
        // Filtrar solo productos con promoción activa
        List<Product> productsOnSale = allProducts.stream()
                .filter(Product::hasActivePromotion)
                .collect(java.util.stream.Collectors.toList());
        
        // Convertir a ProductResponse
        return toProductResponseList(productsOnSale);
    }





}
