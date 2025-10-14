package com.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "promotions")
public class Promotion {

    public Promotion() {
    }

    public Promotion(String name, String description, PromotionType type, double value, 
                    LocalDateTime startDate, LocalDateTime endDate, Product product) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.product = product;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PromotionType type;

    @Column(name = "value", nullable = false)
    private double value; // porcentaje o monto fijo según el tipo

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    /**
     * Verifica si la promoción está vigente (activa y dentro del rango de fechas)
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active && 
               now.isAfter(startDate) && 
               now.isBefore(endDate);
    }

    /**
     * Calcula el precio con descuento aplicado
     */
    public double calculateDiscountedPrice(double originalPrice) {
        if (!isValid()) {
            return Math.round(originalPrice * 100.0) / 100.0;
        }
        
        double discountedPrice;
        switch (type) {
            case PERCENTAGE:
                discountedPrice = originalPrice - (originalPrice * value / 100);
                break;
            case FIXED_AMOUNT:
                discountedPrice = originalPrice - value;
                discountedPrice = Math.max(discountedPrice, 0); // No puede ser negativo
                break;
            default:
                discountedPrice = originalPrice;
                break;
        }
        
        // Redondear a 2 decimales
        return Math.round(discountedPrice * 100.0) / 100.0;
    }
}