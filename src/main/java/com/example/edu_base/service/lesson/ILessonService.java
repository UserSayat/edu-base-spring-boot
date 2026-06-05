package com.example.edu_base.service.lesson;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;

import java.util.List;

public interface ILessonService {
    LessonResponse addLesson(LessonRequest request) throws ServerException;
    LessonWithAttendanceResponse getLessonById(Long id) throws ServerException;
    List<LessonResponse> getLessons() throws ServerException;

    LessonResponse editLesson(Long id, LessonRequest request) throws ServerException;
    void deleteLesson(Long id) throws ServerException;
}
