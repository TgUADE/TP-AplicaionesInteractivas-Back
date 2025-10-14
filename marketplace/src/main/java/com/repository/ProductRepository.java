package com.repository;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE p.description = :description")
    List<Product> findByDescription(@Param("description") String description);

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    List<Product> findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.price = :price")
    List<Product> findByPrice(@Param("price") double price);

    @Query("SELECT p FROM Product p WHERE p.stock = :stock")
    List<Product> findByStock(@Param("stock") long stock);
    
    // Método optimizado que carga productos con categorías (evita MultipleBagFetchException)
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "ORDER BY p.name")
    List<Product> findAllWithCategories();
    
    // Método para cargar promociones de productos específicos
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.promotions " +
           "WHERE p IN :products")
    List<Product> findPromotionsForProducts(@Param("products") List<Product> products);
    
    // Método para cargar imágenes de productos específicos
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p IN :products")
    List<Product> findImagesForProducts(@Param("products") List<Product> products);
}
