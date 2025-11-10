package com.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name = "cart")
public class Cart {

    public Cart() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartProduct> cartProducts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = java.time.LocalDateTime.now();
        }
    }

    /**
     * Método helper para agregar un producto al carrito manteniendo la consistencia bidireccional
     */
    public void addCartProduct(CartProduct cartProduct) {
        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }
        cartProducts.add(cartProduct);
        cartProduct.setCart(this);
    }

    /**
     * Método helper para remover un producto del carrito manteniendo la consistencia bidireccional
     */
    public void removeCartProduct(CartProduct cartProduct) {
        if (cartProducts != null) {
            cartProducts.remove(cartProduct);
            cartProduct.setCart(null);
        }
    }

    /**
     * Limpia todos los productos del carrito
     */
    public void clearProducts() {
        if (cartProducts != null) {
            cartProducts.clear();
        }
    }

}