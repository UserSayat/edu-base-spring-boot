package com.example.edu_base.repository.teacher;

import com.example.edu_base.entity.Teacher;

import java.util.List;
import java.util.Optional;

public interface ITeacherRepository {
    Teacher save(Teacher teacher);
    Optional<Teacher> findById(Long id);
    List<Teacher> findAll();
    boolean update(Teacher teacher);
    boolean deleteById(Long id);
}
