package com.example.edu_base.service.teacher;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;

import java.util.List;

public interface ITeacherService {

    TeacherResponse addTeacher(TeacherRequest request) throws ServerException;
    TeacherResponse getTeacherById(Long id) throws ServerException;
    List<TeacherResponse> getTeachers() throws ServerException;
    TeacherResponse editTeacher(Long id, TeacherRequest request) throws ServerException;
    void deleteTeacher(Long id) throws ServerException;

}
