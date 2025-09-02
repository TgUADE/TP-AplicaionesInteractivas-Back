package com.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.entity.Role;
import com.entity.dto.RoleRequest;
import com.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role create(RoleRequest request) {
        final String name = normalizeName(request.getName());
        if (roleRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rol ya existe");
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(request.getDescription());
        
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getById(UUID id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> list() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Role update(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (request.getName() != null) {
            String newName = normalizeName(request.getName());
            if (!newName.equalsIgnoreCase(role.getName()) && roleRepository.existsByName(newName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con ese nombre");
            }
            role.setName(newName);
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }
        roleRepository.deleteById(id);
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del rol es obligatorio");
        }
        return name.trim();
    }
}
