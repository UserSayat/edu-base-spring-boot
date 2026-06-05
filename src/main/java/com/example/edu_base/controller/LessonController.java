package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.service.lesson.LessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Validated
@RequestMapping("api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<LessonResponse>> addLesson(@Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(lessonService.addLesson(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<LessonWithAttendanceResponse>> getLessonById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(new CommonResponse<>(lessonService.getLessonById(id)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<LessonResponse>>> getLessons() {
        return ResponseEntity.ok(new CommonResponse<>(lessonService.getLessons()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<LessonResponse>> editLesson(@PathVariable @Min(1) Long id,
                                                                     @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(lessonService.editLesson(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteLesson(@PathVariable @Min(1) Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}
