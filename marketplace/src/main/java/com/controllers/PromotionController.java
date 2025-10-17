package com.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Promotion;
import com.entity.dto.PromotionRequest;
import com.entity.dto.PromotionResponse;
import com.entity.dto.PromotionSummary;
import com.exceptions.ProductNotFoundException;
import com.exceptions.PromotionNotFoundException;
import com.exceptions.PromotionValidationException;
import com.service.PromotionService;

@RestController
@RequestMapping("promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    // Obtener todas las promociones
    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        List<PromotionResponse> response = promotionService.toPromotionResponseList(promotions);
        return ResponseEntity.ok(response);
    }

    // Obtener promoción por ID
    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable UUID promotionId) 
            throws PromotionNotFoundException {
        Optional<Promotion> result = promotionService.getPromotionById(promotionId);
        if (result.isPresent()) {
            PromotionResponse response = promotionService.toPromotionResponse(result.get());
            return ResponseEntity.ok(response);
        } else {
            throw new PromotionNotFoundException();
        }
    }

    // Crear nueva promoción - Solo ADMIN
    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@RequestBody PromotionRequest promotionRequest) 
            throws ProductNotFoundException, PromotionValidationException {
        Promotion result = promotionService.createPromotion(promotionRequest);
        PromotionResponse response = promotionService.toPromotionResponse(result);
        return ResponseEntity.created(URI.create("/promotions/" + result.getId())).body(response);
    }

    // Actualizar promoción - Solo ADMIN
    @PutMapping("/{promotionId}")
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable UUID promotionId, 
            @RequestBody PromotionRequest promotionRequest) 
            throws PromotionNotFoundException, ProductNotFoundException, PromotionValidationException {
        Promotion result = promotionService.updatePromotion(promotionId, promotionRequest);
        PromotionResponse response = promotionService.toPromotionResponse(result);
        return ResponseEntity.ok(response);
    }

    // Eliminar promoción - Solo ADMIN
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID promotionId) 
            throws PromotionNotFoundException {
        promotionService.deletePromotion(promotionId);
        return ResponseEntity.noContent().build();
    }

    // Obtener promociones de un producto específico
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByProduct(@PathVariable UUID productId) {
        List<Promotion> promotions = promotionService.getPromotionsByProduct(productId);
        List<PromotionResponse> response = promotionService.toPromotionResponseList(promotions);
        return ResponseEntity.ok(response);
    }

    // Obtener promoción activa de un producto
    @GetMapping("/product/{productId}/active")
    public ResponseEntity<PromotionResponse> getActivePromotionByProduct(@PathVariable UUID productId) {
        Optional<Promotion> result = promotionService.getActivePromotionByProduct(productId);
        if (result.isPresent()) {
            PromotionResponse response = promotionService.toPromotionResponse(result.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener todas las promociones activas
    @GetMapping("/active")
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        List<Promotion> promotions = promotionService.getActivePromotions();
        List<PromotionResponse> response = promotionService.toPromotionResponseList(promotions);
        return ResponseEntity.ok(response);
    }

    // Obtener resumen de promociones (para dashboards)
    @GetMapping("/summary")
    public ResponseEntity<List<PromotionSummary>> getPromotionsSummary(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<Promotion> promotions = activeOnly ? 
            promotionService.getActivePromotions() : 
            promotionService.getAllPromotions();
        List<PromotionSummary> response = promotionService.toPromotionSummaryList(promotions);
        return ResponseEntity.ok(response);
    }

    // Activar promoción - Solo ADMIN
    @PutMapping("/{promotionId}/activate")
    public ResponseEntity<PromotionResponse> activatePromotion(@PathVariable UUID promotionId) 
            throws PromotionNotFoundException {
        promotionService.activatePromotion(promotionId);
        Optional<Promotion> result = promotionService.getPromotionById(promotionId);
        PromotionResponse response = promotionService.toPromotionResponse(result.get());
        return ResponseEntity.ok(response);
    }

    // Desactivar promoción - Solo ADMIN
    @PutMapping("/{promotionId}/deactivate")
    public ResponseEntity<PromotionResponse> deactivatePromotion(@PathVariable UUID promotionId) 
            throws PromotionNotFoundException {
        promotionService.deactivatePromotion(promotionId);
        Optional<Promotion> result = promotionService.getPromotionById(promotionId);
        PromotionResponse response = promotionService.toPromotionResponse(result.get());
        return ResponseEntity.ok(response);
    }

    // Limpiar promociones expiradas - Solo ADMIN
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupExpiredPromotions() {
        promotionService.cleanupExpiredPromotions();
        return ResponseEntity.ok("Expired promotions have been deactivated");
    }

    // Obtener promociones expiradas
    @GetMapping("/expired")
    public ResponseEntity<List<PromotionResponse>> getExpiredPromotions() {
        List<Promotion> promotions = promotionService.getExpiredPromotions();
        List<PromotionResponse> response = promotionService.toPromotionResponseList(promotions);
        return ResponseEntity.ok(response);
    }
}