package com.example.edu_base.repository.subject;

import com.example.edu_base.entity.Subject;
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
public class SubjectRepository implements ISubjectRepository {

    private JdbcTemplate jdbcTemplate;

    public SubjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Subject> subjectRowMapper = (rs, rowNum) -> {
        Subject subject = new Subject();

        subject.setId(rs.getLong("id"));
        subject.setSubjectName(rs.getString("subject_name"));

        OffsetDateTime createdOffset = rs.getObject("created_at", OffsetDateTime.class);
        if (createdOffset != null)
            subject.setCreatedAt(createdOffset.toZonedDateTime());

        OffsetDateTime updatedOffset = rs.getObject("updated_at", OffsetDateTime.class);
        if (updatedOffset != null)
            subject.setUpdatedAt(updatedOffset.toZonedDateTime());

        return subject;
    };

    @Override
    public Subject save(Subject subject) {
        String sql = "INSERT INTO subjects (subject_name, created_at, updated_at) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now();
        subject.setCreatedAt(now);
        subject.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, subject.getSubjectName());
            ps.setObject(2, subject.getCreatedAt());
            ps.setObject(3, subject.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            subject.setId(keyHolder.getKey().longValue());

        return subject;
    }

    @Override
    public Optional<Subject> findById(Long id) {
        String sql = "SELECT id, subject_name, created_at, updated_at FROM subjects WHERE id = ?";

        try {
            Subject subject = jdbcTemplate.queryForObject(sql, subjectRowMapper, id);
            return Optional.ofNullable(subject);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Subject> findAll() {
        String sql = "SELECT id, subject_name, created_at, updated_at FROM subjects";

        return jdbcTemplate.query(sql, subjectRowMapper);
    }

    @Override
    public boolean update(Subject subject) {
        String sql = "UPDATE subjects SET subject_name = ?, updated_at = ? WHERE id = ?";
        int rowAffected = jdbcTemplate.update(sql,
                subject.getSubjectName(),
                subject.getUpdatedAt(),
                subject.getId());

        return rowAffected > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        int rowAffected = jdbcTemplate.update(sql, id);

        return rowAffected > 0;
    }
}
