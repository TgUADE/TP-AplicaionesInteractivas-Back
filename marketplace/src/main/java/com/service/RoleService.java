package com.service;

import java.util.List;
import java.util.UUID;

import com.entity.Role;
import com.entity.dto.RoleRequest;

public interface RoleService {
    Role create(RoleRequest request);
    Role getById(UUID id);
    List<Role> list();
    Role update(UUID id, RoleRequest request);
    void delete(UUID id);
}
