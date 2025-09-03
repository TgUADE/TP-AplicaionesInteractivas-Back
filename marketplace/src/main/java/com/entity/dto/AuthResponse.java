package com.entity.dto;

import com.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
    private LocalDateTime createdAt;
    private String message;
    
    // Constructor para respuestas de éxito con usuario
    public AuthResponse(UUID id, String name, String email, Role role, String phone, 
                       String address, String city, String state, String zip, String country, 
                       LocalDateTime createdAt) {
        this(id, name, email, role, phone, address, city, state, zip, country, createdAt, null);
    }
    
    // Constructor para respuestas con solo mensaje
    public AuthResponse(String message) {
        this(null, null, null, null, null, null, null, null, null, null, null, message);
    }
}