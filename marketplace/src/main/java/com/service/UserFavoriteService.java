package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.entity.Product;
import com.entity.UserFavorite;
import com.entity.dto.FavoriteResponse;

public interface UserFavoriteService {
    
    // Agregar producto a favoritos
    UserFavorite addToFavorites(UUID userId, UUID productId);
    
    // Remover producto de favoritos
    void removeFromFavorites(UUID userId, UUID productId);
    
    // Verificar si un producto es favorito
    boolean isFavorite(UUID userId, UUID productId);
    
    // Obtener todos los favoritos de un usuario
    List<UserFavorite> getUserFavorites(UUID userId);
    
    // Obtener solo los productos favoritos de un usuario
    List<Product> getUserFavoriteProducts(UUID userId);
    
    
    // Toggle favorito (agregar si no existe, remover si existe)
    boolean toggleFavorite(UUID userId, UUID productId);
    
    // Obtener IDs de productos favoritos de un usuario (para verificación rápida)
    List<UUID> getUserFavoriteProductIds(UUID userId);
    
    // Convertir UserFavorite a FavoriteResponse
    FavoriteResponse toFavoriteResponse(UserFavorite userFavorite);
    
    // Obtener favoritos como FavoriteResponse
    List<FavoriteResponse> getUserFavoritesAsResponse(UUID userId);
    
    // ===== MÉTODOS OPTIMIZADOS ADICIONALES =====
    
    // Verificar múltiples productos como favoritos (batch operation)
    List<UUID> getFavoriteProductIds(UUID userId, List<UUID> productIds);
    
    // Obtener productos más favoritos (estadísticas)
    List<Object[]> getMostFavoritedProducts();
    
    // Buscar favorito específico con relaciones optimizadas
    Optional<UserFavorite> getFavoriteWithRelations(UUID userId, UUID productId);
}
