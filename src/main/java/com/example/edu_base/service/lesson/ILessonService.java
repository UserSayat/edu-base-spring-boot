package com.example.edu_base.service.lesson;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;

public interface ILessonService {
    LessonResponse addLesson(LessonRequest request) throws ServerException;
    //LessonWithAttendanceResponse getLessonById(LessonWithAttendanceRequest request);
    //List<LessonResponse> getLessons(LessonRequest request);

    LessonResponse editLesson(Long id, LessonRequest request) throws ServerException;
    void deleteLesson(Long id) throws ServerException;
}
