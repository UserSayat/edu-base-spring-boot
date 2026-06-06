package com.example.edu_base.repository.studentGroup;

import com.example.edu_base.entity.StudentGroup;

import java.util.List;
import java.util.Optional;

public interface IStudentGroupRepository {
    StudentGroup save(StudentGroup studentGroup);
    Optional<StudentGroup> findById(long id);
    List<StudentGroup> findAll();
    boolean update(StudentGroup studentGroup);
    boolean deleteById(long id);
    Optional<StudentGroup> findByGroupName(String name);
}
