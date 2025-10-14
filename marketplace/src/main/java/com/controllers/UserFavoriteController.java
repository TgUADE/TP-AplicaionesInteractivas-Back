package com.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Product;
import com.entity.User;
import com.entity.UserFavorite;
import com.entity.dto.FavoriteResponse;
import com.service.UserFavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * Obtiene el usuario autenticado del contexto de seguridad
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    // 1. Obtener favoritos del usuario actual (extraído del JWT)
    @GetMapping("/my-favorites")
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites() {
        User authenticatedUser = getAuthenticatedUser();
        List<FavoriteResponse> favorites = userFavoriteService.getUserFavoritesAsResponse(authenticatedUser.getId());
        return ResponseEntity.ok(favorites);
    }
    
    

    // Obtener solo los productos favoritos del usuario actual
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getMyFavoriteProducts() {
        User authenticatedUser = getAuthenticatedUser();
        List<Product> products = userFavoriteService.getUserFavoriteProducts(authenticatedUser.getId());
        return ResponseEntity.ok(products);
    }

    // Obtener IDs de productos favoritos del usuario actual (para verificación rápida)
    @GetMapping("/product-ids")
    public ResponseEntity<List<UUID>> getMyFavoriteProductIds() {
        User authenticatedUser = getAuthenticatedUser();
        List<UUID> productIds = userFavoriteService.getUserFavoriteProductIds(authenticatedUser.getId());
        return ResponseEntity.ok(productIds);
    }

    // 2. Agregar producto a favoritos (sin userId en URL)
    @PostMapping("/products/{productId}")
    public ResponseEntity<UserFavorite> addToMyFavorites(@PathVariable UUID productId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            UserFavorite favorite = userFavoriteService.addToFavorites(authenticatedUser.getId(), productId);
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 3. Eliminar producto de favoritos (sin userId en URL)
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> removeFromMyFavorites(@PathVariable UUID productId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            userFavoriteService.removeFromFavorites(authenticatedUser.getId(), productId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Toggle favorito del usuario actual
    @PostMapping("/products/{productId}/toggle")
    public ResponseEntity<Boolean> toggleMyFavorite(@PathVariable UUID productId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            boolean added = userFavoriteService.toggleFavorite(authenticatedUser.getId(), productId);
            return ResponseEntity.ok(added);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    

    
}
