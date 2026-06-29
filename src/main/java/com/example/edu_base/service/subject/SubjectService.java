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
        Subject subject = new Subject(null,
                request.getSubjectName(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));

        return toSubjectResponse(subjectRepository.save(subject));
    }

    @Override
    public SubjectResponse getSubjectById(long id) throws ServerException {
        log.info("getting subject by id: {}", id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("subject: " + id + " not found"));

        return toSubjectResponse(subject);
    }

    @Override
    public List<SubjectResponse> getSubjects() throws ServerException {
        log.info("get all subjects");
        return subjectRepository.findAll()
                .stream()
                .map(this::toSubjectResponse)
                .toList();
    }

    @Override
    public SubjectResponse editSubject(long id, SubjectRequest request) throws ServerException {
        log.info("editing subject by id: {}", id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("subject: " + id + " not found"));

        subject.setSubjectName(request.getSubjectName());
        subject.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

        subjectRepository.update(subject);

        return toSubjectResponse(subject);
    }

    @Override
    public void deleteSubject(long id) throws ServerException {
        log.info("deleting subject by id: {}", id);
        List<Lesson> lessons = lessonRepository.findBySubjectId(id);
        if (!lessons.isEmpty())
            throw new IllegalArgumentException("can not delete subject while lessons with it exist");

        subjectRepository.deleteById(id);
    }

    public SubjectResponse toSubjectResponse(Subject subject) {
        return new SubjectResponse(subject.getId(),
                subject.getSubjectName(),
                subject.getCreatedAt(),
                subject.getUpdatedAt());
    }
}
