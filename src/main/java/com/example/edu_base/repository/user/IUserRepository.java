package com.example.edu_base.repository.user;

import com.example.edu_base.common.Role;
import com.example.edu_base.entity.User;

import java.util.Optional;
import java.util.Set;

public interface IUserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    Set<Role> findRolesByUserId(long id);
    void saveRoleToUser(long userId, Role role);
}
