package com.example.edu_base.repository.lesson;

import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Student;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LessonRepository implements ILessonRepository {

    private final JdbcTemplate jdbcTemplate;

    public LessonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Lesson> lessonRowMapper = (rs, rowNum) -> new Lesson (
            rs.getLong("id"),
            rs.getLong("subject_id"),
            rs.getObject("date", LocalDate.class),
            rs.getLong("pair_number"),
            rs.getLong("teacher_id"),
            rs.getLong("student_group_id"),
            rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
            rs.getObject("updated_at", OffsetDateTime.class).toZonedDateTime());

    @Override
    public Lesson save(Lesson lesson) {
        String sql = """
                    INSERT INTO lessons (subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now();
        lesson.setCreatedAt(now);
        lesson.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, lesson.getSubjectId());
                ps.setObject(2, lesson.getDate());
                ps.setLong(3, lesson.getPairNumber());
                ps.setLong(4, lesson.getTeacherId());
                ps.setLong(5, lesson.getStudentGroupId());
                ps.setObject(6, lesson.getCreatedAt());
                ps.setObject(7, lesson.getUpdatedAt());

                return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            lesson.setId(keyHolder.getKey().longValue());

        return lesson;
    }

    @Override
    public Optional<Lesson> findById(long id) {
        String sql = "SELECT id, subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at FROM lessons WHERE id = ?";

        try {
            Lesson lesson = jdbcTemplate.queryForObject(sql, lessonRowMapper, id);
            return Optional.ofNullable(lesson);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Lesson> findAll() {
        String sql = "SELECT id, subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at FROM lessons";
        return jdbcTemplate.query(sql, lessonRowMapper);
    }

    @Override
    public boolean update(Lesson lesson) {
        String sql = "UPDATE lessons SET subject_id = ?, date = ?, pair_number = ?, teacher_id = ?, student_group_id = ?, updated_at = ? WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql,
                lesson.getSubjectId(),
                lesson.getDate(),
                lesson.getPairNumber(),
                lesson.getTeacherId(),
                lesson.getStudentGroupId(),
                lesson.getUpdatedAt(),
                lesson.getId());

        return rowAffected > 0;
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM lessons WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql, id);

        return rowAffected > 0;
    }

    @Override
    public List<Lesson> findByTeacherId(long id) {
        String sql = "SELECT id, subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at FROM lessons WHERE teacher_id = ?";
        return jdbcTemplate.query(sql, lessonRowMapper, id);
    }

    @Override
    public List<Lesson> findBySubjectId(long id) {
        String sql = "SELECT id, subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at FROM lessons WHERE subject_id = ?";
        return jdbcTemplate.query(sql, lessonRowMapper, id);
    }

    @Override
    public Optional<Lesson> findByDateAndPairNumber(LocalDate date, long pairNumber) {
        String sql = """
                SELECT id, subject_id, date, pair_number, teacher_id, student_group_id, created_at, updated_at
                FROM lessons WHERE date = ? AND pair_number = ?
                """;
        try {
            Lesson lesson = jdbcTemplate.queryForObject(sql, lessonRowMapper, date, pairNumber);
            return Optional.ofNullable(lesson);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

//    @Override
//    public List<Long> findStudentsByStudentGroupId(long id) {
//        String sql = """
//        SELECT s.id FROM students s
//        JOIN lessons l ON s.student_group_id = l.student_group_id
//        WHERE s.student_group_id = ?
//        """;
//        return jdbcTemplate.queryForList(sql, Long.class, id);
//    }
}
