package com.example.edu_base.service.student;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.student.IStudentRepository;
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

    public List<StudentResponse> getStudentsByGroup(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentService.getStudentsByGroup: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            return studentRepository.findByStudentGroupId(id)
                    .stream()
                    .map(this::toStudentResponse)
                    .toList();
        } catch (Exception e) {
            log.error("error in method StudentService.getStudentsByGroup");
            throw new ServerException("db error: getStudentsByGroup", e, 201, null);
        }
    }

    public StudentResponse getStudentById(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentService.getStudentById: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student: " + id + " not found"));
            return toStudentResponse(student);
        } catch (Exception e) {
            log.error("error in method StudentService.getStudentById");
            throw new ServerException("db error: getStudentById", e, 202, null);
        }
    }

    public StudentResponse addStudent(StudentRequest request) throws ServerException {
        if (request.getId() != null) {
            log.error("error in method StudentService.addStudentGroup: id should be null");
            throw new IllegalArgumentException("id should be null!");
        }
        try {
            Student student = new Student();
            student.setLastName(request.getLastName());
            student.setFirstName(request.getFirstName());
            student.setMiddleName(request.getMiddleName());
            student.setStudentGroupId(request.getStudentGroupId());
            student.setStatus(request.getStudentStatus());
            student.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            student.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            return toStudentResponse(studentRepository.save(student));
        } catch (Exception e) {
            log.error(e.getCause().toString() + LocalDateTime.now().toString());
            throw new ServerException("db error: addStudent", e, 203, null);
        }
    }

    public StudentResponse editStudent(Long id, StudentRequest request) throws ServerException {
        if (request.getId() == null) {
            log.error("error in method StudentService.editStudent: id should not be null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student: " + id + " not found"));

            StudentGroup studentGroup = IStudentGroupRepository
                    .findById(request.getStudentGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("group with id: " + request.getStudentGroupId() + " doesn't exist"));

            student.setLastName(request.getLastName());
            student.setFirstName(request.getFirstName());
            student.setMiddleName(request.getMiddleName());
            student.setStudentGroupId(request.getStudentGroupId());
            student.setStatus(request.getStudentStatus());
            student.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            studentRepository.update(student);

            return toStudentResponse(student);
        } catch (Exception e) {
            throw new ServerException("db error: editStudent()", e, 204, null);
        }
    }

    public void deleteStudent(Long id) throws ServerException {
        if (id == null) {
            log.error("id should not be null");
            throw new IllegalArgumentException("id should not be null!");
        }
        boolean deleted = studentRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("Student wasn't delete", 205, null);
    }

    public StudentResponse toStudentResponse(Student student) {
        StudentGroup studentGroup = IStudentGroupRepository
                .findById(student.getStudentGroupId())
                .orElseThrow(() -> new IllegalArgumentException("group with id: " + student.getStudentGroupId() + " doesn't exist"));


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
