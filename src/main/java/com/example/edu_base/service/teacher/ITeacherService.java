package com.example.edu_base.service.teacher;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;

import java.util.List;

public interface ITeacherService {

    TeacherResponse addTeacher(TeacherRequest request) throws ServerException;
    TeacherResponse getTeacherById(long id) throws ServerException;
    List<TeacherResponse> getTeachers() throws ServerException;
    TeacherResponse editTeacher(long id, TeacherRequest request) throws ServerException;
    void deleteTeacher(long id) throws ServerException;

}
