package com.example.edu_base.service.teacher;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Teacher;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.teacher.ITeacherRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class TeacherService implements ITeacherService {

    private final ITeacherRepository teacherRepository;
    private final ILessonRepository lessonRepository;

    public TeacherService(ITeacherRepository teacherRepository, ILessonRepository lessonRepository) {
        this.teacherRepository = teacherRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public TeacherResponse addTeacher(TeacherRequest request) throws ServerException {
        log.info("adding teacher: {} {} {}",
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName());
        try {
            Teacher teacher = new Teacher(null,
                    request.getLastName(),
                    request.getFirstName(),
                    request.getMiddleName(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));

            return toTeacherResponse(teacherRepository.save(teacher));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to add teacher: {} {} {}",
                    request.getLastName(),
                    request.getFirstName(),
                    request.getMiddleName(), e);
            throw new ServerException(message, e, 3001, null);
        }
    }

    @Override
    public TeacherResponse getTeacherById(long id) throws ServerException {
        log.info("getting teacher by id: {}", id);
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("teacher: " + id + " not found"));
            return toTeacherResponse(teacher);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get teacher by id: {}", id, e);
            throw new ServerException(message, e, 3002, null);
        }
    }

    @Override
    public List<TeacherResponse> getTeachers() throws ServerException {
        try {
            return teacherRepository.findAll()
                    .stream()
                    .map(this::toTeacherResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get teachers");
            throw new ServerException(message, e, 3003, null);
        }
    }

    @Override
    public TeacherResponse editTeacher(long id, TeacherRequest request) throws ServerException {
        log.info("editing teacher by id: {}", id);
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("teacher: " + id + " not found"));

            teacher.setLastName(request.getLastName());
            teacher.setFirstName(request.getFirstName());
            teacher.setMiddleName(request.getMiddleName());
            teacher.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            teacherRepository.update(teacher);

            return toTeacherResponse(teacher);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to edit teacher by id: {}", id, e);
            throw new ServerException(message, e, 3004, null);
        }
    }

    @Override
    public void deleteTeacher(long id) throws ServerException {
        log.info("deleting teacher by id: {}", id);
        List<Lesson> lessons = lessonRepository.findByTeacherId(id);
        if (!lessons.isEmpty())
            throw new IllegalArgumentException("can not delete teacher while lessons with him exist");


        if (!teacherRepository.deleteById(id)) {
            log.warn("failed to delete teacher by id: {}", id);
            throw new ServerException("teacher wasn't delete", 3005, null);
        }
    }

    public TeacherResponse toTeacherResponse(Teacher teacher) {
        return new TeacherResponse(teacher.getId(),
                teacher.getLastName(),
                teacher.getFirstName(),
                teacher.getMiddleName(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt());
    }
}
