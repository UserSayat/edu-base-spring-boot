package com.example.edu_base.repository.attendance;

import com.example.edu_base.entity.Attendance;

import java.util.List;
import java.util.Optional;

public interface IAttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(long id);
    List<Attendance> findAll();
    boolean update(long id, Attendance attendance);
    boolean deleteById(long id);
    Optional<Attendance> findByStudentId(long studentId);
    boolean deleteByStudentId(long id);
}
