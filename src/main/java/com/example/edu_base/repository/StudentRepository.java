package com.example.edu_base.repository;

import java.util.List;
import com.example.edu_base.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByStudentGroupId(Long id);
}
