package com.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.User;
import com.entity.dto.AuthResponse;
import com.entity.dto.LoginRequest;
import com.entity.dto.RegisterRequest;
import com.exceptions.UserDuplicateException;
import com.exceptions.UserInvalidCredentialsException;
import com.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws UserDuplicateException {
        User user = userService.register(request);
        
        AuthResponse response = new AuthResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getPhone(),
            user.getAddress(),
            user.getCity(),
            user.getState(),
            user.getZip(),
            user.getCountry(),
            user.getCreatedAt()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws UserInvalidCredentialsException {
        User user = userService.login(request);
        
        AuthResponse response = new AuthResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getPhone(),
            user.getAddress(),
            user.getCity(),
            user.getState(),
            user.getZip(),
            user.getCountry(),
            user.getCreatedAt()
        );
        
        return ResponseEntity.ok(response);
    }
}