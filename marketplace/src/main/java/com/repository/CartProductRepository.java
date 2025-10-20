package com.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.CartProduct;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
    
    List<CartProduct> findByCartId(UUID cartId);
    
    Optional<CartProduct> findByCartIdAndProductId(UUID cartId, UUID productId);
    
    void deleteByCartIdAndProductId(UUID cartId, UUID productId);
    
    void deleteByCartId(UUID cartId);

    @Modifying
    @Query(value = "DELETE FROM cart_products WHERE product_id = :productId", nativeQuery = true)
    void deleteByProductId(@Param("productId") UUID productId);


    
    // ===== MÉTODOS OPTIMIZADOS PARA EVITAR N+1 =====
    
     // Cargar productos del carrito con toda la información del producto
     @Query("SELECT cp FROM CartProduct cp " +
     "LEFT JOIN FETCH cp.product p " +
     "LEFT JOIN FETCH p.category " +
     "WHERE cp.cart.id = :cartId " +
     "ORDER BY cp.createdAt")
List<CartProduct> findByCartIdWithProducts(@Param("cartId") UUID cartId);

// Cargar producto específico del carrito con información completa
@Query("SELECT cp FROM CartProduct cp " +
     "LEFT JOIN FETCH cp.product p " +
     "LEFT JOIN FETCH p.category " +
     "WHERE cp.cart.id = :cartId AND cp.product.id = :productId")
Optional<CartProduct> findByCartIdAndProductIdWithProduct(@Param("cartId") UUID cartId,@Param("productId") UUID productId); 
    
}
