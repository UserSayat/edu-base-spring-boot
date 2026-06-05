package com.example.edu_base.repository.student;

import org.springframework.stereotype.Repository;
import com.example.edu_base.common.StudentStatus;
import com.example.edu_base.entity.Student;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository implements IStudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> new Student(
        rs.getLong("id"),
        rs.getString("last_name"),
        rs.getString("first_name"),
        rs.getString("middle_name"),
        StudentStatus.valueOf(rs.getObject("student_status").toString()),
        rs.getLong("student_group_id"),
        rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
        rs.getObject("updated_at", OffsetDateTime.class).toZonedDateTime());

    @Override
    public Student save(Student student) {
        String sql = "INSERT INTO students (last_name, first_name, middle_name, student_status, student_group_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now();
        long timestamp = now.toInstant().toEpochMilli();
        student.setCreatedAt(now);
        student.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getLastName());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getMiddleName());
            ps.setString(4, student.getStatus().name());
            ps.setLong(5,student.getStudentGroupId());
            ps.setObject(6, student.getCreatedAt());
            ps.setObject(7, student.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            student.setId(keyHolder.getKey().longValue());

        return student;
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sql = "SELECT id, last_name, first_name, middle_name, student_status, student_group_id, created_at, updated_at FROM students WHERE id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, id);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET last_name = ?, first_name = ?, middle_name = ?, student_status = ?, student_group_id = ?, updated_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                student.getLastName(),
                student.getFirstName(),
                student.getMiddleName(),
                student.getStatus().name(),
                student.getStudentGroupId(),
                student.getUpdatedAt(),
                student.getId());

        return rowsAffected > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        return rowsAffected > 0;
    }

    @Override
    public List<Student> findByStudentGroupId(Long studentGroupId) {
        String sql = "SELECT id, last_name, first_name, middle_name, student_status, student_group_id, created_at, updated_at FROM students WHERE student_group_id = ?";
        return jdbcTemplate.query(sql, studentRowMapper, studentGroupId);
    }
}
