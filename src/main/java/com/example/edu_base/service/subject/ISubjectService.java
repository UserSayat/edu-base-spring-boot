package com.example.edu_base.service.subject;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;

import java.util.List;

public interface ISubjectService {
    SubjectResponse addSubject(SubjectRequest request) throws ServerException;
    SubjectResponse getSubjectById(long id) throws ServerException;
    List<SubjectResponse> getSubjects() throws ServerException;
    SubjectResponse editSubject(long id, SubjectRequest request) throws ServerException;
    void deleteSubject(long id) throws ServerException;
}
