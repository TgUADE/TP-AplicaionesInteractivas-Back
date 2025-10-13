package com.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.entity.Promotion;
import com.entity.PromotionType;
import com.entity.dto.PromotionRequest;
import com.entity.dto.PromotionResponse;
import com.entity.dto.PromotionSummary;
import com.exceptions.ProductNotFoundException;
import com.exceptions.PromotionNotFoundException;
import com.exceptions.PromotionValidationException;

public interface PromotionService {
    
    // CRUD básico
    List<Promotion> getAllPromotions();
    Optional<Promotion> getPromotionById(UUID promotionId);
    Promotion createPromotion(PromotionRequest request) throws ProductNotFoundException, PromotionValidationException;
    Promotion updatePromotion(UUID promotionId, PromotionRequest request) throws PromotionNotFoundException, ProductNotFoundException, PromotionValidationException;
    void deletePromotion(UUID promotionId) throws PromotionNotFoundException;
    
    // Operaciones específicas de negocio
    List<Promotion> getPromotionsByProduct(UUID productId);
    List<Promotion> getActivePromotions();
    Optional<Promotion> getActivePromotionByProduct(UUID productId);
    
    // Conversión a DTOs
    PromotionResponse toPromotionResponse(Promotion promotion);
    List<PromotionResponse> toPromotionResponseList(List<Promotion> promotions);
    PromotionSummary toPromotionSummary(Promotion promotion);
    List<PromotionSummary> toPromotionSummaryList(List<Promotion> promotions);
    
    // Operaciones de gestión
    void activatePromotion(UUID promotionId) throws PromotionNotFoundException;
    void deactivatePromotion(UUID promotionId) throws PromotionNotFoundException;
    List<Promotion> getExpiredPromotions();
    void cleanupExpiredPromotions();
    
    // Validaciones
    boolean isPromotionValid(Promotion promotion);
    double calculateDiscountedPrice(UUID productId, double originalPrice);
    void validatePromotionRequest(PromotionRequest request) throws PromotionValidationException, ProductNotFoundException;
    void validatePromotionDates(LocalDateTime startDate, LocalDateTime endDate) throws PromotionValidationException;
    void validatePromotionValue(PromotionType type, double value) throws PromotionValidationException;
    boolean hasActivePromotionConflict(UUID productId, LocalDateTime startDate, LocalDateTime endDate, UUID excludePromotionId);
}