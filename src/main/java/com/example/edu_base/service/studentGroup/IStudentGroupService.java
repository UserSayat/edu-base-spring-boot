package com.example.edu_base.service.studentGroup;

import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;

import java.util.List;

public interface IStudentGroupService {
    List<StudentGroupResponse> getStudentGroups();
    StudentGroupResponse getStudentGroupById(long id);
    StudentGroupResponse addStudentGroup(StudentGroupRequest request);
    StudentGroupResponse editStudentGroup(long id, StudentGroupRequest request);
    void deleteStudentGroup(long id);
}
