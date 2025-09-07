package com.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.entity.Role;
import com.entity.User;
import com.entity.dto.UserRequest;
import com.repository.RoleRepository;
import com.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public User create(UserRequest request) {
        final String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El teléfono ya está registrado");
        }

        User u = new User();
        applyRequest(u, request, true);
        u.setEmail(email);
        u.setPassword(request.getPassword()); 

        // Aca asignamos el rol por defecto si no se especifica
        if (u.getRole() == null) {
            Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rol USER no encontrado"));
            u.setRole(defaultRole);
        }

        return userRepository.save(u);
    }
    @Override
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> list() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User update(UUID id, UserRequest request) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Si el email cambia hay que validarlo de nuevo
        if (request.getEmail() != null) {
            String newEmail = normalizeEmail(request.getEmail());
            if (!newEmail.equalsIgnoreCase(u.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
            }
            u.setEmail(newEmail);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            u.setPassword(request.getPassword()); 
        }

        applyRequest(u, request, false);

        return userRepository.save(u);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

  
    private void applyRequest(User u, UserRequest r, boolean isCreate) {
        if (r.getName() != null)            u.setName(r.getName());
        if (r.getPhone() != null)           u.setPhone(r.getPhone());
        if (r.getAddress() != null)         u.setAddress(r.getAddress());
        if (r.getCity() != null)            u.setCity(r.getCity());
        if (r.getState() != null)           u.setState(r.getState());
        if (r.getZip() != null)             u.setZip(r.getZip());
        if (r.getCountry() != null)         u.setCountry(r.getCountry());
        
        // Si especifica roleId, le asignamos el rol
        if (r.getRoleId() != null) {
            Role role = roleRepository.findById(r.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido"));
            u.setRole(role);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
