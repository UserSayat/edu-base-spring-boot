package com.example.edu_base.repository.teacher;

import com.example.edu_base.entity.Teacher;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TeacherRepository implements ITeacherRepository {

    private JdbcTemplate jdbcTemplate;

    public TeacherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Teacher> teacherRowMapper = (rs, rowNum) -> {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getLong("id"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setMiddleName(rs.getString("middle_name"));

        OffsetDateTime createdOffset = rs.getObject("created_at", OffsetDateTime.class);
        if (createdOffset != null)
            teacher.setCreatedAt(createdOffset.toZonedDateTime());

        OffsetDateTime updatedOffset = rs.getObject("updated_at", OffsetDateTime.class);
        if (updatedOffset != null)
            teacher.setUpdatedAt(updatedOffset.toZonedDateTime());

        return teacher;
    };

    @Override
    public Teacher save(Teacher teacher) {
        String sql = "INSERT INTO teachers (last_name, first_name, middle_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now();
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, teacher.getLastName());
            ps.setString(2, teacher.getFirstName());
            ps.setString(3, teacher.getMiddleName());
            ps.setObject(4, teacher.getCreatedAt());
            ps.setObject(5, teacher.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            teacher.setId(keyHolder.getKey().longValue());

        return teacher;
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        String sql = "SELECT id, last_name, first_name, middle_name, created_at, updated_at FROM teachers WHERE id = ?";
        try {
            Teacher teacher = jdbcTemplate.queryForObject(sql, teacherRowMapper, id);
            return Optional.ofNullable(teacher);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Teacher> findAll() {
        String sql = "SELECT id, last_name, first_name, middle_name, created_at, updated_at FROM teachers";
        return jdbcTemplate.query(sql, teacherRowMapper);
    }

    @Override
    public boolean update(Teacher teacher) {
        String sql = "UPDATE teachers SET last_name = ?, first_name = ?, middle_name = ?, updated_at = ? WHERE id  = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                teacher.getLastName(),
                teacher.getFirstName(),
                teacher.getMiddleName(),
                teacher.getUpdatedAt(),
                teacher.getId());

        return rowsAffected > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        return rowsAffected > 0;
    }
}
