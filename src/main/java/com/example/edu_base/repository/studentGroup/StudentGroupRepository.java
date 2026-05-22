package com.example.edu_base.repository.studentGroup;

import com.example.edu_base.entity.StudentGroup;
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
public class StudentGroupRepository implements IStudentGroupRepository {

    private JdbcTemplate jdbcTemplate;

    public StudentGroupRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StudentGroup> studentGroupRowMapper = (rs, rowNum) -> {
        StudentGroup studentGroup = new StudentGroup();

        studentGroup.setId(rs.getLong("id"));
        studentGroup.setGroupName(rs.getString("group_name"));

        OffsetDateTime createdOffset = rs.getObject("created_at", OffsetDateTime.class);
        if (createdOffset != null)
            studentGroup.setCreatedAt(createdOffset.toZonedDateTime());

        OffsetDateTime updatedOffset = rs.getObject("updated_at", OffsetDateTime.class);
        if (updatedOffset != null)
            studentGroup.setUpdatedAt(updatedOffset.toZonedDateTime());

        return studentGroup;
    };

    public StudentGroup save(StudentGroup studentGroup) {
        String sql = "INSERT INTO student_groups SET group_name = ?, created_at = ?, updated_at = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now();
        studentGroup.setCreatedAt(now);
        studentGroup.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, studentGroup.getGroupName());
            ps.setObject(2, studentGroup.getCreatedAt());
            ps.setObject(3, studentGroup.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            studentGroup.setId(keyHolder.getKey().longValue());

        return studentGroup;
    }

    public Optional<StudentGroup> findById(Long id) {
        String sql = "SELECT id, group_name, created_at, updated_at FROM student_groups WHERE id = ?";
        try {
            StudentGroup studentGroup = jdbcTemplate.queryForObject(sql, studentGroupRowMapper, id);
            return Optional.ofNullable(studentGroup);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<StudentGroup> findAll() {
        String sql = "SELECT id, group_name, created_at, updated_at FROM student_groups";
        return jdbcTemplate.query(sql, studentGroupRowMapper);
    }

    public boolean update(StudentGroup studentGroup) {
        String sql = "UPDATE student_groups SET group_name = ?, updated_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                studentGroup.getGroupName(),
                studentGroup.getUpdatedAt(),
                studentGroup.getId());

        return rowsAffected > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM student_groups WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        return rowsAffected > 0;
    }

    public Optional<StudentGroup> findByGroupName(String groupName) {
        String sql = "SELECT id, group_name, created_at, updated_at FROM student_groups WHERE group_name = ?";
        try {
            StudentGroup studentGroup = jdbcTemplate.queryForObject(sql, studentGroupRowMapper, groupName);
            return Optional.ofNullable(studentGroup);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
