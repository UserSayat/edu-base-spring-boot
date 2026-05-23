package com.example.edu_base.service.student;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;

import java.util.List;

public interface IStudentService {
    List<StudentResponse> getStudentsByGroup(Long id) throws ServerException;
    StudentResponse getStudentById(Long id) throws ServerException;
    StudentResponse addStudent(StudentRequest request) throws ServerException;
    StudentResponse editStudent(Long id, StudentRequest request) throws ServerException;
    void deleteStudent(Long id) throws ServerException;
}
