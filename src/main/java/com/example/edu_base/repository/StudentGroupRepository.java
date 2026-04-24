package com.example.edu_base.repository;

import com.example.edu_base.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    StudentGroup findByGroupName(String name);
}
