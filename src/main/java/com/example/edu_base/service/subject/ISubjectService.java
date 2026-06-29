package com.example.edu_base.service.subject;

import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;

import java.util.List;

public interface ISubjectService {
    SubjectResponse addSubject(SubjectRequest request);
    SubjectResponse getSubjectById(long id);
    List<SubjectResponse> getSubjects();
    SubjectResponse editSubject(long id, SubjectRequest request);
    void deleteSubject(long id);
}
