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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("api/students")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<StudentResponse>> addStudent(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse<>(studentService.addStudent(request)));
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<CommonResponse<List<StudentResponse>>> getStudentsByGroup(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(new CommonResponse<>(studentService.getStudentsByGroup(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentResponse>> getStudentById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(new CommonResponse<>(studentService.getStudentById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentResponse>> editStudent(@PathVariable @Min(1) long id,
                                                                       @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(studentService.editStudent(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable @Min(1) long id) {
        studentService.deleteStudent(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}
