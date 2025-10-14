package com.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.UserFavorite;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UUID> {
    
    // Buscar favorito específico por usuario y producto
    Optional<UserFavorite> findByUserIdAndProductId(UUID userId, UUID productId);
    
    // Obtener todos los favoritos de un usuario con productos optimizados
    @Query("SELECT DISTINCT uf FROM UserFavorite uf " +
           "LEFT JOIN FETCH uf.user u " +
           "LEFT JOIN FETCH uf.product p " +
           "LEFT JOIN FETCH p.category c " +
           "WHERE uf.user.id = :userId " +
           "ORDER BY uf.createdAt DESC")
    List<UserFavorite> findByUserIdWithProducts(@Param("userId") UUID userId);
    
    // Verificar si un producto es favorito de un usuario
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    
    // Eliminar favorito por usuario y producto
    void deleteByUserIdAndProductId(UUID userId, UUID productId);
    
    // Contar favoritos de un usuario
    long countByUserId(UUID userId);
    
    // Obtener usuarios que tienen un producto como favorito
    @Query("SELECT uf FROM UserFavorite uf " +
           "LEFT JOIN FETCH uf.user " +
           "WHERE uf.product.id = :productId")
    List<UserFavorite> findByProductIdWithUsers(@Param("productId") UUID productId);
    
    // Obtener favoritos de un usuario (solo IDs para verificación rápida)
    @Query("SELECT uf.product.id FROM UserFavorite uf WHERE uf.user.id = :userId")
    List<UUID> findProductIdsByUserId(@Param("userId") UUID userId);
    
    // ===== MÉTODOS OPTIMIZADOS ADICIONALES =====
    
    // Buscar favorito específico con todas las relaciones cargadas
    @Query("SELECT uf FROM UserFavorite uf " +
           "LEFT JOIN FETCH uf.user u " +
           "LEFT JOIN FETCH uf.product p " +
           "LEFT JOIN FETCH p.category c " +
           "WHERE uf.user.id = :userId AND uf.product.id = :productId")
    Optional<UserFavorite> findByUserIdAndProductIdWithRelations(@Param("userId") UUID userId, 
                                                                @Param("productId") UUID productId);
    
    // Obtener todos los favoritos con relaciones completas (para admin)
    @Query("SELECT DISTINCT uf FROM UserFavorite uf " +
           "LEFT JOIN FETCH uf.user u " +
           "LEFT JOIN FETCH uf.product p " +
           "LEFT JOIN FETCH p.category c " +
           "ORDER BY uf.createdAt DESC")
    List<UserFavorite> findAllWithRelations();
    
    // Obtener favoritos de múltiples usuarios (optimizado para batch operations)
    @Query("SELECT DISTINCT uf FROM UserFavorite uf " +
           "LEFT JOIN FETCH uf.user u " +
           "LEFT JOIN FETCH uf.product p " +
           "LEFT JOIN FETCH p.category c " +
           "WHERE uf.user.id IN :userIds " +
           "ORDER BY uf.user.id, uf.createdAt DESC")
    List<UserFavorite> findByUserIdsWithProducts(@Param("userIds") List<UUID> userIds);
    
    // Obtener productos más favoritos (estadísticas)
    @Query("SELECT uf.product.id, COUNT(uf) as favoriteCount " +
           "FROM UserFavorite uf " +
           "GROUP BY uf.product.id " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findMostFavoritedProducts();
    
    // Obtener productos favoritos con información completa (sin metadata de UserFavorite)
    @Query("SELECT DISTINCT p FROM UserFavorite uf " +
           "JOIN uf.product p " +
           "LEFT JOIN FETCH p.category c " +
           "WHERE uf.user.id = :userId " +
           "ORDER BY uf.createdAt DESC")
    List<com.entity.Product> findProductsByUserIdOptimized(@Param("userId") UUID userId);
    
    // Verificación batch de favoritos (para múltiples productos)
    @Query("SELECT uf.product.id FROM UserFavorite uf " +
           "WHERE uf.user.id = :userId AND uf.product.id IN :productIds")
    List<UUID> findFavoriteProductIds(@Param("userId") UUID userId, 
                                     @Param("productIds") List<UUID> productIds);
   
    
    // Obtener productos favoritos con imágenes
    @Query("SELECT DISTINCT p FROM UserFavorite uf " +
           "JOIN uf.product p " +
           "LEFT JOIN FETCH p.images i " +
           "WHERE uf.user.id = :userId")
    List<com.entity.Product> findProductsByUserIdWithImages(@Param("userId") UUID userId);
    
    // Obtener productos favoritos con promociones
    @Query("SELECT DISTINCT p FROM UserFavorite uf " +
           "JOIN uf.product p " +
           "LEFT JOIN FETCH p.promotions pr " +
           "WHERE uf.user.id = :userId")
    List<com.entity.Product> findProductsByUserIdWithPromotions(@Param("userId") UUID userId);
}
