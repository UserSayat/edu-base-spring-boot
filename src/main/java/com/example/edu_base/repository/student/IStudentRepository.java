package com.example.edu_base.repository.student;

import java.util.List;
import java.util.Optional;

import com.example.edu_base.entity.Student;

public interface IStudentRepository {
    Student save (Student student);
    Optional<Student> findById(long id);
    boolean update(Student student);
    boolean deleteById(long id);
    List<Student> findByStudentGroupId(long studentGroupId);
}
