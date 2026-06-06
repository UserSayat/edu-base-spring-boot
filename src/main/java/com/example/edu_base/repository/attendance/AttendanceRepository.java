package com.example.edu_base.repository.attendance;

import com.example.edu_base.entity.Attendance;
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
import java.util.List;
import java.util.Optional;

@Repository
public class AttendanceRepository implements IAttendanceRepository {

    private final JdbcTemplate jdbcTemplate;

    public AttendanceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Attendance> attendanceRowMapper = (rs, rowNum) -> new Attendance(
            rs.getLong("id"),
            rs.getLong("lesson_id"),
            rs.getLong("student_id"),
            rs.getBoolean("is_present"),
            rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
            rs.getObject("updated_at", OffsetDateTime.class).toZonedDateTime());

    @Override
    public Attendance save(Attendance attendance) {
        String sql = "INSERT INTO attendances (lesson_id, student_id, is_present, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, attendance.getLessonId());
            ps.setLong(2, attendance.getStudentId());
            ps.setBoolean(3, attendance.isPresent());
            ps.setObject(4, now.toOffsetDateTime());
            ps.setObject(5, now.toOffsetDateTime());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null)
            attendance.setId(keyHolder.getKey().longValue());

        return attendance;
    }

    @Override
    public Optional<Attendance> findById(long id) {
        String sql = "SELECT id, lesson_id, student_id, is_present, created_at, updated_at FROM attendances WHERE id = ?";

        try {
            Attendance attendance = jdbcTemplate.queryForObject(sql, attendanceRowMapper, id);
            return Optional.ofNullable(attendance);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Attendance> findAll() {
        String sql = "SELECT id, lesson_id, student_id, is_present, created_at, updated_at FROM attendances";
        return jdbcTemplate.query(sql, attendanceRowMapper);
    }

    @Override
    public boolean update(long id, Attendance attendance) {
        String sql = "UPDATE attendances SET lesson_id = ?, student_id = ?, is_present = ?, updated_at = ? WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql,
                attendance.getLessonId(),
                attendance.getStudentId(),
                attendance.isPresent(),
                attendance.getUpdatedAt(),
                attendance.getId());

        return rowAffected > 0;
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM attendances WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql, id);

        return rowAffected > 0;
    }

    @Override
    public Optional<Attendance> findByStudentId(long studentId) {
        String sql = "SELECT id, lesson_id, student_id, is_present, created_at, updated_at FROM attendances WHERE student_id = ?";

        try {
            Attendance attendance = jdbcTemplate.queryForObject(sql, attendanceRowMapper, studentId);
            return Optional.ofNullable(attendance);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteByStudentId(long id) {
        String sql = "DELETE FROM attendances WHERE student_id = ?";

        int rowAffected = jdbcTemplate.update(sql, id);

        return rowAffected > 0;
    }
}
