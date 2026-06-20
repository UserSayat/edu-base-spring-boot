package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.service.teacher.ITeacherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("api/teachers")
public class TeacherController {

    private final ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<TeacherResponse>> addTeacher(@Valid @RequestBody TeacherRequest request) {
        log.info("request to add teacher: {} {} {}",
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName());

        TeacherResponse response = teacherService.addTeacher(request);

        log.info("teacher: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> getTeacherById(@PathVariable @Min(1) long id) {
        log.info("request to get teacher by id: {}", id);

        TeacherResponse response = teacherService.getTeacherById(id);

        log.info("teacher: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<CommonResponse<List<TeacherResponse>>> getTeachers() {
        log.info("request to get teachers");

        List<TeacherResponse> response = teacherService.getTeachers();

        log.info("teachers successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> editTeacher(@PathVariable @Min(1) long id,
                                                                       @Valid @RequestBody TeacherRequest request) {
        log.info("request to edit teacher by id: {}", id);

        TeacherResponse response = teacherService.editTeacher(id, request);

        log.info("teacher: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable @Min(1) long id) {
        log.info("request to delete teacher by id: {}", id);

        teacherService.deleteTeacher(id);

        log.info("teacher: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
