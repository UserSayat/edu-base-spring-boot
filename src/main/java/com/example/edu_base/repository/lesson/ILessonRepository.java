package com.example.edu_base.repository.lesson;

import com.example.edu_base.entity.Lesson;

import java.util.List;
import java.util.Optional;

public interface ILessonRepository {
    Lesson save(Lesson lesson);
    Optional<Lesson> findById(Long id);
    List<Lesson> findAll();
    boolean update(Lesson lesson);
    boolean deleteById(Long id);
}
