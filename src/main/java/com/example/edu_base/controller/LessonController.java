package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.service.lesson.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<LessonResponse>> addLesson(@RequestBody LessonRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(lessonService.addLesson(request)));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(203, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<LessonResponse>> editLesson(@PathVariable Long id,
                                                                     @RequestBody LessonRequest request) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(lessonService.editLesson(id, request)));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(204, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteLesson(@PathVariable Long id) {
        try {
            lessonService.deleteLesson(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(205, e.getMessage(), null));
        }
    }
}
