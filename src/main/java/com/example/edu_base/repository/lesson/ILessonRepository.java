package com.example.edu_base.repository.lesson;

import com.example.edu_base.entity.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ILessonRepository {
    Lesson save(Lesson lesson);
    Optional<Lesson> findById(long id);
    List<Lesson> findAll();
    boolean update(Lesson lesson);
    boolean deleteById(long id);
    List<Lesson> findByTeacherId(long id);
    List<Lesson> findBySubjectId(long id);
    Optional<Lesson> findByDateAndPairNumber(LocalDate date, long pairNumber);
    List<Long> findStudentsByStudentGroupId(long id);
}
