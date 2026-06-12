package com.example.edu_base.service.studentGroup;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;

import java.util.List;

public interface IStudentGroupService {
    List<StudentGroupResponse> getStudentGroups() throws ServerException;
    StudentGroupResponse getStudentGroupById(long id) throws ServerException;
    StudentGroupResponse addStudentGroup(StudentGroupRequest request) throws ServerException;
    StudentGroupResponse editStudentGroup(long id, StudentGroupRequest request) throws ServerException;
    void deleteStudentGroup(long id) throws ServerException;
}
