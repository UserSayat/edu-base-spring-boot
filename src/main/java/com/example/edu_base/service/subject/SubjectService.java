package com.example.edu_base.service.subject;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.entity.Subject;
import com.example.edu_base.repository.subject.ISubjectRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SubjectService implements ISubjectService {

    private final ISubjectRepository subjectRepository;

    public SubjectService(ISubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public SubjectResponse addSubject(SubjectRequest request) throws ServerException {
        if (request.getId() != null) {
            throw new IllegalArgumentException("id should be null!");
        }
        try {
            Subject subject = new Subject();
            subject.setSubjectName(request.getSubjectName());
            subject.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            subject.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            return toSubjectResponse(subjectRepository.save(subject));
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 203, null);
        }
    }

    @Override
    public SubjectResponse getSubjectById(Long id) throws ServerException {
        if (id == null)
            throw new IllegalArgumentException("id should not be null!");
        try {
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("subject: " + id + " not found"));

            return toSubjectResponse(subject);
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 202, null);
        }
    }

    @Override
    public List<SubjectResponse> getSubjects() throws ServerException {
        try {
            return subjectRepository.findAll()
                    .stream()
                    .map(this::toSubjectResponse)
                    .toList();
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 202, null);
        }
    }

    @Override
    public SubjectResponse editSubject(Long id, SubjectRequest request) throws ServerException {
        if (id == null)
            throw new IllegalArgumentException("id should not be null!");
        try {
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("subject: " + id + " not found"));

            subject.setSubjectName(request.getSubjectName());
            subject.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            subjectRepository.update(subject);

            return toSubjectResponse(subject);
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 204, null);
        }
    }

    @Override
    public void deleteSubject(Long id) throws ServerException {
        if (id == null)
            throw new IllegalArgumentException("id should not be null!");

        boolean deleted = subjectRepository.deleteById(id);

        if (!deleted)
            throw new ServerException("Teacher wasn't delete", 205, null);
    }

    public SubjectResponse toSubjectResponse(Subject subject) {
        return new SubjectResponse(subject.getId(),
                subject.getSubjectName(),
                subject.getCreatedAt(),
                subject.getUpdatedAt());
    }
}
