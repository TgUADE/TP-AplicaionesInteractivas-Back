package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entity.Product;
import com.entity.User;
import com.entity.UserFavorite;
import com.entity.dto.FavoriteResponse;
import com.exceptions.ProductNotFoundException;
import com.repository.UserFavoriteRepository;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public UserFavorite addToFavorites(UUID userId, UUID productId) {
        // Verificar si ya existe
        if (userFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }
        
        User user = userService.getById(userId);
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));
        
        UserFavorite favorite = new UserFavorite(user, product);
        return userFavoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFromFavorites(UUID userId, UUID productId) {
        if (!userFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("El producto no está en favoritos");
        }
        
        userFavoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(UUID userId, UUID productId) {
        return userFavoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFavorite> getUserFavorites(UUID userId) {
        return userFavoriteRepository.findByUserIdWithProducts(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getUserFavoriteProducts(UUID userId) {
        // Usar método optimizado que devuelve directamente productos
        return userFavoriteRepository.findProductsByUserIdOptimized(userId);
    }

    

    @Override
    @Transactional
    public boolean toggleFavorite(UUID userId, UUID productId) {
        if (userFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            removeFromFavorites(userId, productId);
            return false; // Removido
        } else {
            addToFavorites(userId, productId);
            return true; // Agregado
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getUserFavoriteProductIds(UUID userId) {
        return userFavoriteRepository.findProductIdsByUserId(userId);
    }

    @Override
    public FavoriteResponse toFavoriteResponse(UserFavorite userFavorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(userFavorite.getId());
        response.setUserId(userFavorite.getUser().getId());
        response.setProduct(userFavorite.getProduct());
        response.setCreatedAt(userFavorite.getCreatedAt());
        response.setFavorite(true);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getUserFavoritesAsResponse(UUID userId) {
        return userFavoriteRepository.findByUserIdWithProducts(userId)
                .stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS OPTIMIZADOS ADICIONALES =====

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getFavoriteProductIds(UUID userId, List<UUID> productIds) {
        return userFavoriteRepository.findFavoriteProductIds(userId, productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostFavoritedProducts() {
        return userFavoriteRepository.findMostFavoritedProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserFavorite> getFavoriteWithRelations(UUID userId, UUID productId) {
        return userFavoriteRepository.findByUserIdAndProductIdWithRelations(userId, productId);
    }
}
