package com.example.edu_base.repository.user;

import com.example.edu_base.common.Role;
import com.example.edu_base.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userDetailsRowMapper = (rs, rowNum) -> {
        User user = new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getBoolean("active"),
            null,
            rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
            rs.getObject("updated_at", OffsetDateTime.class).toZonedDateTime());

        Set<Role> roles = findRolesByUserId(user.getId());
        user.setRoles(roles);

        return user;
    };

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (username, password, active, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isActive());
            ps.setObject(4, user.getCreatedAt());
            ps.setObject(5, user.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());

            for (Role role : user.getRoles()) {
                saveRoleToUser(user.getId(), role);
            }
        }

        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                    SELECT id, username, password, active, created_at, updated_at
                    FROM users WHERE username = ?
                """;
        try {
            User user = jdbcTemplate.queryForObject(sql, userDetailsRowMapper, username);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Set<Role> findRolesByUserId(long id) {
        String sql = """
                SELECT name FROM roles r
                JOIN user_roles ur ON r.id = ur.role_id
                WHERE ur.user_id = ?
                """;

        Set<String> roles = new HashSet<>(jdbcTemplate.queryForList(sql, String.class, id));

        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveRoleToUser(long userId, Role role) {
        String sql = """
                INSERT INTO user_roles ur (user_id, role_id)
                VALUES (?, (SELECT r.id FROM roles r WHERE r.name = ?))
                """;

        jdbcTemplate.update(sql, userId, role);
    }
}
