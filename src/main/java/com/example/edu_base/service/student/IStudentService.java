package com.example.edu_base.service.student;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;

import java.util.List;

public interface IStudentService {
    List<StudentResponse> getStudentsByGroup(long id) throws ServerException;
    StudentResponse getStudentById(long id) throws ServerException;
    StudentResponse addStudent(StudentRequest request) throws ServerException;
    StudentResponse editStudent(long id, StudentRequest request) throws ServerException;
    void deleteStudent(long id) throws ServerException;
}
