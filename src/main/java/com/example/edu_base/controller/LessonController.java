package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.service.lesson.LessonService;
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
@RequestMapping("api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<LessonResponse>> addLesson(@Valid @RequestBody LessonRequest request) {
        log.info("request to add lesson for student group: {}", request.getStudentGroupId());

        LessonResponse response = lessonService.addLesson(request);

        log.info("lesson: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<LessonWithAttendanceResponse>> getLessonById(@PathVariable @Min(1) long id) {
        log.info("request to get lesson by id: {}", id);

        LessonWithAttendanceResponse response = lessonService.getLessonById(id);

        log.info("lesson: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<List<LessonResponse>>> getLessons() {
        log.info("request to get lessons");

        List<LessonResponse> response = lessonService.getLessons();

        log.info("lessons successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<LessonResponse>> editLesson(@PathVariable @Min(1) long id,
                                                                     @Valid @RequestBody LessonRequest request) {
        log.info("request to edit lesson by id: {}", id);

        LessonResponse response = lessonService.editLesson(id, request);

        log.info("lesson: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<Void>> deleteLesson(@PathVariable @Min(1) long id) {
        log.info("request to delete lesson by id: {}", id);

        lessonService.deleteLesson(id);

        log.info("lesson: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}