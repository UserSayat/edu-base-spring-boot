package com.example.edu_base.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.StudentGroup.StudentGroupResponse;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.StudentGroupRepository;
import com.example.edu_base.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;

    public StudentService(StudentRepository studentRepository, StudentGroupRepository studentGroupRepository) {
        this.studentRepository = studentRepository;
        this.studentGroupRepository = studentGroupRepository;
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

    @Transactional
    public StudentResponse addStudent(StudentRequest request) throws ServerException {
        if (request.getId() != null) {
            log.error("error in method StudentService.addStudentGroup: id should be null");
            throw new IllegalArgumentException("id should be null!");
        }
        try {
            //TODO упростить логику возможно Student(Entity) должен хрпнить только название
            //TODO или идентификатор группы, а не весь объект
            StudentGroup studentGroup = studentGroupRepository.findByGroupName(request.getStudentGroup());

            Student student = new Student();
            student.setLastName(request.getLastName());
            student.setFirstName(request.getFirstName());
            student.setMiddleName(request.getMiddleName());
            student.setStudentGroup(studentGroup);
            student.setStatus(request.getStatus());
            student.setCreatedAt(LocalDateTime.now());
            student.setUpdatedAt(LocalDateTime.now());
            return toStudentResponse(studentRepository.save(student));
        } catch (Exception e) {
            log.error("error in method StudentService.addStudent");
            throw new ServerException("db error: addStudent", e, 203, null);
        }
    }

    public StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getMiddleName(),
                student.getStatus(),
                new StudentGroupResponse(student.getStudentGroup().getId(),
                        student.getStudentGroup().getGroupName(),
                        student.getStudentGroup().getCreatedAt(),
                        student.getStudentGroup().getUpdatedAt()),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }

    @Transactional
    public StudentResponse editStudent(Long id, StudentRequest request) throws ServerException {
        if (request.getId() == null) {
            log.error("error in method StudentService.editStudent: id should not be null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student: " + id + " not found"));

            //TODO упростить логику возможно Student(Entity) должен хрпнить только название
            //TODO или идентификатор группы, а не весь объект
            StudentGroup studentGroup = studentGroupRepository.findByGroupName(request.getStudentGroup());

            student.setLastName(request.getLastName());
            student.setFirstName(request.getFirstName());
            student.setMiddleName(request.getMiddleName());
            student.setStudentGroup(studentGroup);
            student.setStatus(request.getStatus());
            student.setUpdatedAt(LocalDateTime.now());

            return toStudentResponse(student);
        } catch (Exception e) {
            throw new ServerException("db error: editStudent()", e, 204, null);
        }
    }

    @Transactional
    public void deleteStudent(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentService.deleteStudent: id should not be null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            studentGroupRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServerException("db error: deleteStudent()", e, 205, null);
        }
    }
}
