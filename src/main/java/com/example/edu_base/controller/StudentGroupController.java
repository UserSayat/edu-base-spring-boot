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
    public ResponseEntity<CommonResponse<StudentGroupResponse>> addStudentGroup(@Valid @RequestBody StudentGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse<>(studentGroupService.addStudentGroup(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> getStudentGroupById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(new CommonResponse<>(studentGroupService.getStudentGroupById(id)));
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<StudentGroupResponse>>> getStudentGroups() {
         return ResponseEntity.ok(new CommonResponse<>(studentGroupService.getStudentGroups()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> editStudentGroup(@PathVariable @Min(1) Long id,
                                                                                 @Valid @RequestBody StudentGroupRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(studentGroupService.editStudentGroup(id, request)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<CommonResponse<Void>> deleteStudentGroup(@PathVariable @Min(1) Long id) {
        studentGroupService.deleteStudentGroup(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
