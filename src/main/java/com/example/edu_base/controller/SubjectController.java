package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.service.subject.ISubjectService;
import com.sun.net.httpserver.HttpServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final ISubjectService subjectService;

    public SubjectController(ISubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<SubjectResponse>> addSubject(@RequestBody SubjectRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(subjectService.addSubject(request)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> getSubjectById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(subjectService.getSubjectById(id)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<SubjectResponse>>> getSubjects() {
        try {
            return ResponseEntity.ok(new CommonResponse<>(subjectService.getSubjects()));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> editSubject(@PathVariable Long id,
                                                                       @RequestBody SubjectRequest request) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(subjectService.editSubject(id, request)));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }
}
