package com.example.edu_base.service.teacher;

import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;

import java.util.List;

public interface ITeacherService {

    TeacherResponse addTeacher(TeacherRequest request);
    TeacherResponse getTeacherById(long id);
    List<TeacherResponse> getTeachers();
    TeacherResponse editTeacher(long id, TeacherRequest request);
    void deleteTeacher(long id);

}
