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

        Teacher teacher = new Teacher(null,
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));

        return toTeacherResponse(teacherRepository.save(teacher));
    }

    @Override
    public TeacherResponse getTeacherById(long id) throws ServerException {
        log.info("getting teacher by id: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("teacher: " + id + " not found"));
        return toTeacherResponse(teacher);
    }

    @Override
    public List<TeacherResponse> getTeachers() throws ServerException {
        log.info("getting all teachers");
        return teacherRepository.findAll()
                .stream()
                .map(this::toTeacherResponse)
                .toList();
    }

    @Override
    public TeacherResponse editTeacher(long id, TeacherRequest request) throws ServerException {
        log.info("editing teacher by id: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("teacher: " + id + " not found"));

        teacher.setLastName(request.getLastName());
        teacher.setFirstName(request.getFirstName());
        teacher.setMiddleName(request.getMiddleName());
        teacher.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

        teacherRepository.update(teacher);

        return toTeacherResponse(teacher);
    }

    @Override
    public void deleteTeacher(long id) throws ServerException {
        log.info("deleting teacher by id: {}", id);
        List<Lesson> lessons = lessonRepository.findByTeacherId(id);
        if (!lessons.isEmpty())
            throw new IllegalArgumentException("can not delete teacher while lessons with him exist");


        teacherRepository.deleteById(id);
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
