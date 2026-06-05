package com.example.edu_base.service.teacher;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.entity.Teacher;
import com.example.edu_base.repository.teacher.ITeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TeacherService implements ITeacherService {

    private final ITeacherRepository teacherRepository;

    public TeacherService(ITeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public TeacherResponse addTeacher(TeacherRequest request) throws ServerException {
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
            throw new ServerException(message, e, 3001, null);
        }
    }

    @Override
    public TeacherResponse getTeacherById(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("teacher: " + id + " not found"));
            return toTeacherResponse(teacher);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
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
            throw new ServerException(message, e, 3003, null);
        }
    }

    @Override
    public TeacherResponse editTeacher(Long id, TeacherRequest request) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
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
            throw new ServerException(message, e, 3004, null);
        }
    }

    @Override
    public void deleteTeacher(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }

        boolean deleted = teacherRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("teacher wasn't delete", 3005, null);
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
