package com.example.edu_base.service.teacher;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.entity.Teacher;
import com.example.edu_base.repository.teacher.ITeacherRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService implements ITeacherService {

    private final ITeacherRepository teacherRepository;

    public TeacherService(ITeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public TeacherResponse addTeacher(TeacherRequest request) throws ServerException {
        if (request.getId() != null) {
            throw new IllegalArgumentException("id should be null!");
        }
        try {
            Teacher teacher = new Teacher();
            teacher.setLastName(request.getLastName());
            teacher.setFirstName(request.getFirstName());
            teacher.setMiddleName(request.getMiddleName());
            teacher.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            teacher.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            return toTeacherResponse(teacherRepository.save(teacher));
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 203, null);
        }
    }

    @Override
    public TeacherResponse getTeacherById(Long id) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("teacher: " + id + " not found"));
            return toTeacherResponse(teacher);
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 202, null);
        }
    }

    @Override
    public List<TeacherResponse> getTeachers() throws ServerException {
        List<TeacherResponse> teachers = new ArrayList<>();
        try {
            teachers = teacherRepository.findAll()
                    .stream()
                    .map(this::toTeacherResponse)
                    .toList();

            return teachers;
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 201, null);
        }
    }

    @Override
    public TeacherResponse editTeacher(Long id, TeacherRequest request) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("teacher: " + id + " not found"));
            teacher.setLastName(request.getLastName());
            teacher.setFirstName(request.getFirstName());
            teacher.setMiddleName(request.getMiddleName());
            teacher.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            teacherRepository.update(teacher);

            return toTeacherResponse(teacher);
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 204, null);
        }
    }

    @Override
    public void deleteTeacher(Long id) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }

        boolean deleted = teacherRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("Teacher wasn't delete", 205, null);
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
