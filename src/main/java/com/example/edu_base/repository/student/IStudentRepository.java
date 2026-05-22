package com.example.edu_base.repository.student;

import java.util.List;
import java.util.Optional;

import com.example.edu_base.entity.Student;

public interface IStudentRepository {
    Student save (Student student);
    Optional<Student> findById(Long id);
    boolean update(Student student);
    boolean deleteById(Long id);
    List<Student> findByStudentGroupId(Long id);
}
