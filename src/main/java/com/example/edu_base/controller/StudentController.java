package com.example.edu_base.controller;

import java.util.List;
import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.service.student.IStudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("api/students")
public class StudentController {
    //TODO Студент видит список СВОЕЙ группы, данные о посещаемости СВОЕЙ группы

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<StudentResponse>> addStudent(@Valid @RequestBody StudentRequest request) {
        log.info("request to add student: {} {} {}",
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName());

        StudentResponse response = studentService.addStudent(request);

        log.info("student: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse<>(response));
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<CommonResponse<List<StudentResponse>>> getStudentsByGroup(@PathVariable @Min(1) long id) {
        log.info("request to get students by student group: {}", id);

        List<StudentResponse> response = studentService.getStudentsByGroup(id);

        log.info("students successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentResponse>> getStudentById(@PathVariable @Min(1) long id) {
        log.info("request to get student by id: {}", id);

        StudentResponse response = studentService.getStudentById(id);

        log.info("student: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<StudentResponse>> editStudent(@PathVariable @Min(1) long id,
                                                                       @Valid @RequestBody StudentRequest request) {
        log.info("request to edit student by id: {}", id);

        StudentResponse response = studentService.editStudent(id, request);

        log.info("student: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable @Min(1) long id) {
        log.info("request to delete student by id: {}", id);

        studentService.deleteStudent(id);

        log.info("student: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}