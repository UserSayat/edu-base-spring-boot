package com.example.edu_base.service.lesson;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;

import java.util.List;

public interface ILessonService {
    LessonResponse addLesson(LessonRequest request) throws ServerException;
    LessonWithAttendanceResponse getLessonById(long id) throws ServerException;
    List<LessonResponse> getLessons() throws ServerException;

    LessonResponse editLesson(long id, LessonRequest request) throws ServerException;
    void deleteLesson(long id) throws ServerException;
}
