package com.example.edu_base.service.subject;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Subject;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class SubjectService implements ISubjectService {

    private final ISubjectRepository subjectRepository;
    private final ILessonRepository lessonRepository;

    public SubjectService(ISubjectRepository subjectRepository, ILessonRepository lessonRepository) {
        this.subjectRepository = subjectRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public SubjectResponse addSubject(SubjectRequest request) throws ServerException {
        log.info("adding subject: {}", request.getSubjectName());
        try {
            Subject subject = new Subject(null,
                    request.getSubjectName(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));

            return toSubjectResponse(subjectRepository.save(subject));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to add subject: {}", request.getSubjectName(), e);
            throw new ServerException(message, e, 4001, null);
        }
    }

    @Override
    public SubjectResponse getSubjectById(long id) throws ServerException {
        log.info("getting subject by id: {}", id);
        try {
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("subject: " + id + " not found"));

            return toSubjectResponse(subject);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get subject by id: {}", id, e);
            throw new ServerException(message, e, 4002, null);
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
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get subjects", e);
            throw new ServerException(message, e, 4003, null);
        }
    }

    @Override
    public SubjectResponse editSubject(long id, SubjectRequest request) throws ServerException {
        log.info("editing subject by id: {}", id);
        try {
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("subject: " + id + " not found"));

            subject.setSubjectName(request.getSubjectName());
            subject.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            subjectRepository.update(subject);

            return toSubjectResponse(subject);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to edit subject by id: {}", id, e);
            throw new ServerException(message, e, 4004, null);
        }
    }

    @Override
    public void deleteSubject(long id) throws ServerException {
        log.info("deleting subject by id: {}", id);
        List<Lesson> lessons = lessonRepository.findBySubjectId(id);
        if (!lessons.isEmpty())
            throw new IllegalArgumentException("can not delete subject while lessons with it exist");

        boolean deleted = subjectRepository.deleteById(id);

        if (!deleted)
            throw new ServerException("subject wasn't delete", 4005, null);
    }

    public SubjectResponse toSubjectResponse(Subject subject) {
        return new SubjectResponse(subject.getId(),
                subject.getSubjectName(),
                subject.getCreatedAt(),
                subject.getUpdatedAt());
    }
}
