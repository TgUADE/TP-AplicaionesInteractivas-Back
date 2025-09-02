package com.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class RoleRequest {
    
    @NotBlank(message = "El nombre del rol es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String description;
}
