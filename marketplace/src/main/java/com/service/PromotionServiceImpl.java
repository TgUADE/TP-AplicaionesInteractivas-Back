package com.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Product;
import com.entity.Promotion;
import com.entity.PromotionType;
import com.entity.dto.PromotionRequest;
import com.entity.dto.PromotionResponse;
import com.entity.dto.PromotionSummary;
import com.exceptions.ProductNotFoundException;
import com.exceptions.PromotionNotFoundException;
import com.exceptions.PromotionValidationException;
import com.repository.PromotionRepository;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductService productService;

    // CRUD básico
    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getPromotionById(UUID promotionId) {
        return promotionRepository.findById(promotionId);
    }

    @Override
    public Promotion createPromotion(PromotionRequest request) throws ProductNotFoundException, PromotionValidationException {
        // Validar la solicitud completa
        validatePromotionRequest(request);
        
        Product product = productService.getProductById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException());
        
        Promotion promotion = new Promotion(
            request.getName(),
            request.getDescription(),
            request.getType(),
            request.getValue(),
            request.getStartDate(),
            request.getEndDate(),
            product
        );
        
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion updatePromotion(UUID promotionId, PromotionRequest request) 
            throws PromotionNotFoundException, ProductNotFoundException, PromotionValidationException {
        
        Promotion promotion = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new PromotionNotFoundException());
        
        Product product = productService.getProductById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException());
        
        // Validar la solicitud completa, excluyendo la promoción actual de la validación de conflictos
        validatePromotionRequest(request, promotionId);
        
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setType(request.getType());
        promotion.setValue(request.getValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.getActive());
        promotion.setProduct(product);
        
        return promotionRepository.save(promotion);
    }

    @Override
    public void deletePromotion(UUID promotionId) throws PromotionNotFoundException {
        Promotion promotion = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new PromotionNotFoundException());
        promotionRepository.delete(promotion);
    }

    // Operaciones específicas de negocio
    @Override
    public List<Promotion> getPromotionsByProduct(UUID productId) {
        return promotionRepository.findByProductId(productId);
    }

    @Override
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findAllActive(LocalDateTime.now());
    }

    @Override
    public Optional<Promotion> getActivePromotionByProduct(UUID productId) {
        return promotionRepository.findFirstActiveByProductId(productId, LocalDateTime.now());
    }

    // Conversión a DTOs
    @Override
    public PromotionResponse toPromotionResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setName(promotion.getName());
        response.setDescription(promotion.getDescription());
        response.setType(promotion.getType());
        response.setValue(promotion.getValue());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        response.setActive(promotion.getActive());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setProductId(promotion.getProduct().getId());
        response.setProductName(promotion.getProduct().getName());
        response.setOriginalPrice(promotion.getProduct().getPrice());
        response.setDiscountedPrice(promotion.calculateDiscountedPrice(promotion.getProduct().getPrice()));
        response.setDiscountAmount(promotion.getProduct().getPrice() - response.getDiscountedPrice());
        response.setIsValid(promotion.isValid());
        
        return response;
    }

    @Override
    public List<PromotionResponse> toPromotionResponseList(List<Promotion> promotions) {
        return promotions.stream()
                .map(this::toPromotionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionSummary toPromotionSummary(Promotion promotion) {
        PromotionSummary summary = new PromotionSummary();
        summary.setId(promotion.getId());
        summary.setName(promotion.getName());
        summary.setType(promotion.getType());
        summary.setValue(promotion.getValue());
        summary.setStartDate(promotion.getStartDate());
        summary.setEndDate(promotion.getEndDate());
        summary.setActive(promotion.getActive());
        summary.setIsValid(promotion.isValid());
        summary.setProductName(promotion.getProduct().getName());
        
        // Calcular días restantes
        if (promotion.isValid()) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), promotion.getEndDate());
            summary.setDaysRemaining(Math.max(0, daysRemaining));
        } else {
            summary.setDaysRemaining(0L);
        }
        
        return summary;
    }

    @Override
    public List<PromotionSummary> toPromotionSummaryList(List<Promotion> promotions) {
        return promotions.stream()
                .map(this::toPromotionSummary)
                .collect(Collectors.toList());
    }

    // Operaciones de gestión
    @Override
    public void activatePromotion(UUID promotionId) throws PromotionNotFoundException {
        Promotion promotion = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new PromotionNotFoundException());
        promotion.setActive(true);
        promotionRepository.save(promotion);
    }

    @Override
    public void deactivatePromotion(UUID promotionId) throws PromotionNotFoundException {
        Promotion promotion = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new PromotionNotFoundException());
        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getExpiredPromotions() {
        return promotionRepository.findExpiredActive(LocalDateTime.now());
    }

    @Override
    public void cleanupExpiredPromotions() {
        List<Promotion> expiredPromotions = getExpiredPromotions();
        for (Promotion promotion : expiredPromotions) {
            promotion.setActive(false);
            promotionRepository.save(promotion);
        }
    }

    // Validaciones
    @Override
    public boolean isPromotionValid(Promotion promotion) {
        return promotion.isValid();
    }

    @Override
    public double calculateDiscountedPrice(UUID productId, double originalPrice) {
        Optional<Promotion> activePromotion = getActivePromotionByProduct(productId);
        return activePromotion
                .map(promo -> promo.calculateDiscountedPrice(originalPrice))
                .orElse(originalPrice);
    }

    // Implementación de validaciones
    @Override
    public void validatePromotionRequest(PromotionRequest request) throws PromotionValidationException, ProductNotFoundException {
        validatePromotionRequest(request, null);
    }

    /**
     * Validar promoción con posibilidad de excluir una promoción específica de la validación de conflictos
     * @param request Datos de la promoción
     * @param excludePromotionId ID de la promoción a excluir (null para no excluir ninguna)
     */
    private void validatePromotionRequest(PromotionRequest request, UUID excludePromotionId) throws PromotionValidationException, ProductNotFoundException {
        // Validar campos requeridos
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new PromotionValidationException("Promotion name is required");
        }
        
        if (request.getType() == null) {
            throw new PromotionValidationException("Promotion type is required");
        }
        
        if (request.getValue() == null) {
            throw new PromotionValidationException("Promotion value is required");
        }
        
        if (request.getProductId() == null) {
            throw new PromotionValidationException("Product ID is required");
        }

        // Validar fechas
        validatePromotionDates(request.getStartDate(), request.getEndDate());
        
        // Validar valor según el tipo
        validatePromotionValue(request.getType(), request.getValue());
        
        // Verificar que el producto existe
        productService.getProductById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException());
        
        // Verificar conflictos con otras promociones activas, excluyendo la promoción especificada si existe
        if (hasActivePromotionConflict(request.getProductId(), request.getStartDate(), request.getEndDate(), excludePromotionId)) {
            throw new PromotionValidationException("Product already has an active promotion in the specified date range");
        }
    }

    @Override
    public void validatePromotionDates(LocalDateTime startDate, LocalDateTime endDate) throws PromotionValidationException {
        if (startDate == null || endDate == null) {
            throw new PromotionValidationException("Start date and end date are required");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new PromotionValidationException("Start date must be before end date");
        }
        
        if (endDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new PromotionValidationException("End date must be at least 1 hour in the future");
        }
        
        // Validar duración máxima (90 días)
        if (ChronoUnit.DAYS.between(startDate, endDate) > 90) {
            throw new PromotionValidationException("Promotion duration cannot exceed 90 days");
        }
    }

    @Override
    public void validatePromotionValue(PromotionType type, double value) throws PromotionValidationException {
        if (value <= 0) {
            throw new PromotionValidationException("Promotion value must be greater than 0");
        }
        
        switch (type) {
            case PERCENTAGE:
                if (value > 70) {
                    throw new PromotionValidationException("Percentage discount cannot exceed 70%");
                }
                break;
            case FIXED_AMOUNT:
                if (value > 10000) {
                    throw new PromotionValidationException("Fixed discount amount cannot exceed $10,000");
                }
                break;
        }
    }

    @Override
    public boolean hasActivePromotionConflict(UUID productId, LocalDateTime startDate, LocalDateTime endDate, UUID excludePromotionId) {
        List<Promotion> existingPromotions = promotionRepository.findByProductId(productId);
        
        return existingPromotions.stream()
                .filter(p -> excludePromotionId == null || !p.getId().equals(excludePromotionId))
                .filter(Promotion::getActive)
                .anyMatch(p -> {
                    // Verificar solapamiento de fechas
                    return startDate.isBefore(p.getEndDate()) && endDate.isAfter(p.getStartDate());
                });
    }
}