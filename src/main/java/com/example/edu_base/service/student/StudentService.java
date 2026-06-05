package com.example.edu_base.service.student;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentService implements IStudentService {

    private final IStudentRepository studentRepository;
    private final IStudentGroupRepository IStudentGroupRepository;

    public StudentService(IStudentRepository studentRepository, IStudentGroupRepository IStudentGroupRepository) {
        this.studentRepository = studentRepository;
        this.IStudentGroupRepository = IStudentGroupRepository;
    }

    @Override
    public StudentResponse addStudent(StudentRequest request) throws ServerException {
        try {
            Student student = new Student(null,
                    request.getLastName(),
                    request.getFirstName(),
                    request.getMiddleName(),
                    request.getStudentStatus(),
                    request.getStudentGroupId(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));
            return toStudentResponse(studentRepository.save(student));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 2001, null);
        }
    }

    @Override
    public StudentResponse getStudentById(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student: " + id + " not found"));
            return toStudentResponse(student);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 2002, null);
        }
    }

    @Override
    public List<StudentResponse> getStudentsByGroup(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            return studentRepository.findByStudentGroupId(id)
                    .stream()
                    .map(this::toStudentResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 2003, null);
        }
    }

    @Override
    public StudentResponse editStudent(Long id, StudentRequest request) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student: " + id + " not found"));

            StudentGroup studentGroup = IStudentGroupRepository
                    .findById(request.getStudentGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("group with id: " + request.getStudentGroupId() + " not found"));

            student.setLastName(request.getLastName());
            student.setFirstName(request.getFirstName());
            student.setMiddleName(request.getMiddleName());
            student.setStudentGroupId(request.getStudentGroupId());
            student.setStatus(request.getStudentStatus());
            student.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            studentRepository.update(student);

            return toStudentResponse(student);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 2004, null);
        }
    }

    @Override
    public void deleteStudent(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        boolean deleted = studentRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("student wasn't delete", 2005, null);
    }

    public StudentResponse toStudentResponse(Student student) {
        //Вообще это проверяется при добавлении, но на всякий пока буду проверять
        //вдруг группу удалят, а getStudent будет возвращать groupId = null
        StudentGroup studentGroup = IStudentGroupRepository
                .findById(student.getStudentGroupId())
                .orElseThrow(() -> new EntityNotFoundException("group with id: " + student.getStudentGroupId() + " not found"));


        return new StudentResponse(student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getMiddleName(),
                student.getStatus(),
                new StudentGroupResponse(studentGroup.getId(),
                        studentGroup.getGroupName(),
                        studentGroup.getCreatedAt(),
                        studentGroup.getUpdatedAt()),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }
}
