package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.service.subject.ISubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("api/subjects")
public class SubjectController {

    private final ISubjectService subjectService;

    public SubjectController(ISubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<SubjectResponse>> addSubject(@Valid @RequestBody SubjectRequest request) {
        log.info("request to add subject: {}", request.getSubjectName());

        SubjectResponse response = subjectService.addSubject(request);

        log.info("subject: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> getSubjectById(@PathVariable @Min(1) long id) {
        log.info("request to get subject by id: {}", id);

        SubjectResponse response = subjectService.getSubjectById(id);

        log.info("subject: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<SubjectResponse>>> getSubjects() {
        log.info("request to get subjects");

        List<SubjectResponse> response = subjectService.getSubjects();

        log.info("subjects successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> editSubject(@PathVariable @Min(1) long id,
                                                                       @Valid @RequestBody SubjectRequest request) {
        log.info("request to edit subject by id: {}", id);

        SubjectResponse response = subjectService.editSubject(id, request);

        log.info("subject: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteSubject(@PathVariable @Min(1) long id) {
        log.info("request to delete subject by id: {}", id);

        subjectService.deleteSubject(id);

        log.info("subject: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}