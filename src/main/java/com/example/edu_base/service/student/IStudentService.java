package com.example.edu_base.service.student;

import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;

import java.util.List;

public interface IStudentService {
    List<StudentResponse> getStudentsByGroup(long id);
    StudentResponse getStudentById(long id);
    StudentResponse addStudent(StudentRequest request);
    StudentResponse editStudent(long id, StudentRequest request);
    void deleteStudent(long id);
}
