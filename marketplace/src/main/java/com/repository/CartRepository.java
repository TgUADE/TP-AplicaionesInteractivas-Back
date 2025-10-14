package com.repository;

import com.entity.Cart;
import com.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    // Buscar todos los carritos de un usuario
    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1")
    List<Cart> findByUserId(UUID userId);

    // Buscar carritos que contengan cierto producto
    @Query("SELECT c FROM Cart c JOIN c.cartProducts cp WHERE cp.product.id = ?1")
    List<Cart> findByProductsId(UUID productId);
    
    // ===== MÉTODOS OPTIMIZADOS PARA EVITAR N+1 =====
    
    // Cargar todos los carritos con usuarios (evita lazy loading de user)
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.user " +
           "ORDER BY c.createdAt DESC")
    List<Cart> findAllWithUsers();
    
    // Cargar carrito específico con todas sus relaciones
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.id = :cartId")
    Optional<Cart> findByIdWithUser(@Param("cartId") UUID cartId);
    
    // Cargar carritos de un usuario con todas las relaciones necesarias
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.user.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<Cart> findByUserIdWithUser(@Param("userId") UUID userId);
    
    // Cargar cartProducts para carritos específicos (segunda consulta optimizada)
    @Query("SELECT DISTINCT cp FROM CartProduct cp " +
           "LEFT JOIN FETCH cp.product " +
           "LEFT JOIN FETCH cp.product.category " +
           "WHERE cp.cart IN :carts " +
           "ORDER BY cp.createdAt")
    List<CartProduct> findCartProductsForCarts(@Param("carts") List<Cart> carts);
}
