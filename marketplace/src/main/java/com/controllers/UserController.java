package com.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.entity.Role;
import com.entity.User;
import com.entity.dto.UserRequest;
import com.entity.dto.UserResponse;
import com.exceptions.UserDuplicateException;
import com.exceptions.UserNotFoundException;
import com.service.UserService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserRequest request) throws UserDuplicateException {
        User user = userService.create(request);
        return new UserResponse(user);
    }

    /**
     * Obtener el perfil del usuario actual
     */
    @GetMapping("/me")
    public UserResponse getCurrentUser() throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getByEmail(email);
        return new UserResponse(user);
    }

    /**
     * Actualizar el perfil del usuario actual
     */
    @PutMapping("/me")
    public UserResponse updateCurrentUser(@RequestBody UserRequest request) throws UserNotFoundException, UserDuplicateException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getByEmail(email);
        User user = userService.update(currentUser.getId(), request);
        return new UserResponse(user);
    }

    /**
     * Eliminar el perfil del usuario actual
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getByEmail(email);
        userService.delete(currentUser.getId());
    }

    /**
     * Obtener perfil de usuario por ID
     * Solo para administradores
     */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) throws UserNotFoundException {
        validateAdminAccess();
        User user = userService.getById(id);
        return new UserResponse(user);
    }

    /**
     * Listar todos los usuarios
     * Solo para administradores
     */
    @GetMapping
    public List<UserResponse> list() {
        validateAdminAccess();
        return userService.list().stream()
            .map(UserResponse::new)
            .toList();
    }

    /**
     * Actualizar perfil de usuario por ID
     * Solo para administradores
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserRequest request) throws UserNotFoundException, UserDuplicateException {
        validateAdminAccess();
        User user = userService.update(id, request);
        return new UserResponse(user);
    }

    /**
     * Eliminar perfil de usuario por ID
     * Solo para administradores
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) throws UserNotFoundException {
        validateAdminAccess();
        userService.delete(id);
    }

    /**
     * Valida que el usuario actual sea administrador
     * @throws ResponseStatusException si no es administrador
     */
    private void validateAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, 
                "Solo los administradores pueden realizar esta acción"
            );
        }
    }
}
