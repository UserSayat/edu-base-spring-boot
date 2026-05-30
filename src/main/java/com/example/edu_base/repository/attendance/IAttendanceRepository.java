package com.example.edu_base.repository.attendance;

import com.example.edu_base.entity.Attendance;

import java.util.List;
import java.util.Optional;

public interface IAttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(Long id);
    List<Attendance> findAll();
    boolean update(Long id, Attendance attendance);
    boolean deleteById(Long id);
}
