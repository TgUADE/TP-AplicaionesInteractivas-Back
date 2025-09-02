package com.config;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.entity.Role;
import com.entity.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Inicializando datos por defecto...");
        
        // Crear roles por defecto
        createDefaultRoles();
        
        // Crear usuario admin por defecto
        createDefaultAdmin();
        
        log.info("Inicialización de datos completada.");
    }

    private void createDefaultRoles() {
        String[] defaultRoles = {"USER", "ADMIN"};
        String[] descriptions = {
            "Usuario básico que puede comprar", 
            "Administrador del sistema"
        };

        for (int i = 0; i < defaultRoles.length; i++) {
            Optional<Role> existingRole = roleRepository.findByName(defaultRoles[i]);
            if (existingRole.isEmpty()) {
                Role role = new Role(defaultRoles[i], descriptions[i]);
                roleRepository.save(role);
                log.info("Rol creado: {}", defaultRoles[i]);
            }
        }
    }

    private void createDefaultAdmin() {
        String adminEmail = "admin@marketplace.com";
        
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail(adminEmail);
            admin.setPassword("admin123"); // TODO: Hash this password
            admin.setRole(adminRole);
            
            userRepository.save(admin);
            log.info("Usuario admin creado: {}", adminEmail);
            log.warn("IMPORTANTE: Cambiar contraseña del admin en producción!");
        }
    }
} 