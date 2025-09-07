package com.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El nombre es requerido")
    private String name;
    
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es requerido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
}