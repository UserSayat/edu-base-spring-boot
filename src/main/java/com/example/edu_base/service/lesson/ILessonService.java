package com.example.edu_base.service.lesson;

import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;

import java.util.List;

public interface ILessonService {
    LessonResponse addLesson(LessonRequest request);
    LessonWithAttendanceResponse getLessonById(long id);
    List<LessonResponse> getLessons();

    LessonResponse editLesson(long id, LessonRequest request);
    void deleteLesson(long id);
}
