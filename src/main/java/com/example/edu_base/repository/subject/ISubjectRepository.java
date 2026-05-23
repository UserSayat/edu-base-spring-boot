package com.example.edu_base.repository.subject;

import com.example.edu_base.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface ISubjectRepository {
    Subject save(Subject subject);
    Optional<Subject> findById(Long id);
    List<Subject> findAll();
    boolean update(Subject subject);
    boolean deleteById(Long id);
}
