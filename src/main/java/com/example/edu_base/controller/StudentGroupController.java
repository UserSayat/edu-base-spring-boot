package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.service.studentGroup.IStudentGroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("api/groups")
public class StudentGroupController {

    private final IStudentGroupService studentGroupService;

    public StudentGroupController(IStudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> addStudentGroup(@Valid @RequestBody StudentGroupRequest request) {
        log.info("request to add student group: {}", request.getGroupName());

        StudentGroupResponse response = studentGroupService.addStudentGroup(request);

        log.info("student group: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> getStudentGroupById(@PathVariable @Min(1) long id) {
        log.info("request to get student group by id: {}", id);

        StudentGroupResponse response = studentGroupService.getStudentGroupById(id);

        log.info("student group: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CommonResponse<List<StudentGroupResponse>>> getStudentGroups() {
        log.info("request to get student groups");

        List<StudentGroupResponse> response = studentGroupService.getStudentGroups();

        log.info("student group successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> editStudentGroup(@PathVariable @Min(1) long id,
                                                                                 @Valid @RequestBody StudentGroupRequest request) {
        log.info("request to edit student group by id: {}", id);

        StudentGroupResponse response = studentGroupService.editStudentGroup(id, request);

        log.info("student group: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteStudentGroup(@PathVariable @Min(1) long id) {
        log.info("request to delete student group by id");

        studentGroupService.deleteStudentGroup(id);

        log.info("student group: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
