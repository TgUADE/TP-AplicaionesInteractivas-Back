package com.service;

import java.util.List;
import java.util.UUID;

import com.entity.User;
import com.entity.dto.UserRequest;

public interface UserService {
    User create(UserRequest request);
    User getById(UUID userId);
    List<User> list();
    User update(UUID userId, UserRequest request);
    void delete(UUID userId);
}
