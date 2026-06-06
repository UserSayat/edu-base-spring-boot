package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.service.teacher.ITeacherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(teacherService.addTeacher(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> getTeacherById(@PathVariable @Min(1) long id) {
            return ResponseEntity.ok(new CommonResponse<>(teacherService.getTeacherById(id)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TeacherResponse>>> getTeachers() {
        return ResponseEntity.ok(new CommonResponse<>(teacherService.getTeachers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<TeacherResponse>> editTeacher(@PathVariable @Min(1) long id,
                                                                       @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(teacherService.editTeacher(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable @Min(1) long id) {
        teacherService.deleteTeacher(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}
