package com.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Promotion;
import com.entity.PromotionType;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    
    // Buscar promociones por producto
    @Query("SELECT p FROM Promotion p WHERE p.product.id = :productId")
    List<Promotion> findByProductId(@Param("productId") UUID productId);
    
    // Buscar promociones activas de un producto
    @Query("SELECT p FROM Promotion p WHERE p.product.id = :productId AND p.active = true AND p.startDate <= :now AND p.endDate > :now")
    List<Promotion> findActiveByProductId(@Param("productId") UUID productId, @Param("now") LocalDateTime now);
    
    // Buscar la promoción activa más reciente de un producto
    @Query("SELECT p FROM Promotion p WHERE p.product.id = :productId AND p.active = true AND p.startDate <= :now AND p.endDate > :now ORDER BY p.createdAt DESC")
    Optional<Promotion> findFirstActiveByProductId(@Param("productId") UUID productId, @Param("now") LocalDateTime now);
    
    // Buscar todas las promociones activas
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate > :now")
    List<Promotion> findAllActive(@Param("now") LocalDateTime now);
    
    // Buscar promociones expiradas que siguen activas (para limpieza)
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.endDate <= :now")
    List<Promotion> findExpiredActive(@Param("now") LocalDateTime now);
    
    // Buscar promociones por tipo
    @Query("SELECT p FROM Promotion p WHERE p.type = :type")
    List<Promotion> findByType(@Param("type") PromotionType type);
    
    // Buscar promociones por nombre
    @Query("SELECT p FROM Promotion p WHERE p.name = :name")
    List<Promotion> findByName(@Param("name") String name);
    
    // Buscar promociones que están por comenzar
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate > :now")
    List<Promotion> findUpcoming(@Param("now") LocalDateTime now);
}