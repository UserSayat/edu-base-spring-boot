package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.service.subject.ISubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(subjectService.addSubject(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> getSubjectById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(new CommonResponse<>(subjectService.getSubjectById(id)));
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<SubjectResponse>>> getSubjects() {
        return ResponseEntity.ok(new CommonResponse<>(subjectService.getSubjects()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SubjectResponse>> editSubject(@PathVariable @Min(1) Long id,
                                                                       @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(subjectService.editSubject(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteSubject(@PathVariable @Min(1) Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
    }
}
