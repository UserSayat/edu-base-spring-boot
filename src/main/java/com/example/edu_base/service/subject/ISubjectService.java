package com.example.edu_base.service.subject;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;

import java.util.List;

public interface ISubjectService {
    SubjectResponse addSubject(SubjectRequest request) throws ServerException;
    SubjectResponse getSubjectById(Long id) throws ServerException;
    List<SubjectResponse> getSubjects() throws ServerException;
    SubjectResponse editSubject(Long id, SubjectRequest request) throws ServerException;
    void deleteSubject(Long id) throws ServerException;
}
