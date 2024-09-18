package com.example.tasklist.service;

import com.example.tasklist.domain.user.User;

public interface UserService {

    User getById(Long userId);

    User getUserByEmail(String email);

    User update(User user);

    User create(User user);

    boolean isTaskOwner(Long userId, Long taskId);

    void delete(Long userId);
}
