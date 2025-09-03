package com.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartRequest {
    private UUID id;
    // No enviar userId en el body al crear carrito, se toma de la URL
    private UUID userId; // Solo necesario para update
    private List<UUID> productIds;
}
