package com.example.edu_base.repository.user;

import com.example.edu_base.entity.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
