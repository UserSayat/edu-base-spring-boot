package com.example.edu_base.repository.user;

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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userDetailsRowMapper = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getBoolean("active"),
            rs.getString("role"),
            rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
            rs.getObject("updated_at", OffsetDateTime.class).toZonedDateTime()
    );

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (username, password, active, role, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isActive());
            ps.setString(4, user.getRole());
            ps.setObject(5, user.getCreatedAt());
            ps.setObject(6, user.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                    SELECT id, username, password, active, role, created_at, updated_at
                    FROM users WHERE username = ?
                """;
        try {
            User user = jdbcTemplate.queryForObject(sql, userDetailsRowMapper, username);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
