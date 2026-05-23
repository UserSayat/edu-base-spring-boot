package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.service.student.IStudentService;
import com.example.edu_base.service.teacher.ITeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<TeacherResponse>> addTeacher(@RequestBody TeacherRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(teacherService.addTeacher(request)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> getTeacherById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(teacherService.getTeacherById(id)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TeacherResponse>>> getTeachers() {
        try {
            return ResponseEntity.ok(new CommonResponse<>(teacherService.getTeachers()));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> editTeacher(@PathVariable Long id,
                                                                       @RequestBody TeacherRequest request) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(teacherService.editTeacher(id, request)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }
}
