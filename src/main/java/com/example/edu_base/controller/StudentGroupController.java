package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.StudentGroup.StudentGroupRequest;
import com.example.edu_base.dto.StudentGroup.StudentGroupResponse;
import com.example.edu_base.service.StudentGroupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/group")
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    public StudentGroupController(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<StudentGroupResponse>>> getStudentGroups() {
         log.info("Вызван метод getStudentGroups()");
         try {
             return ResponseEntity.ok(new CommonResponse<>(studentGroupService.getStudentGroups()));
         } catch (ServerException e) {
             log.error("Исключение в методе getStudentGroups");
             return ResponseEntity
                     .status(HttpStatus.UNPROCESSABLE_CONTENT)
                     .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
         }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> getStudentGroupById(@PathVariable Long id) {
         log.info("Вызван метод getStudentGroupById({})", id);
         try {
             return ResponseEntity.ok(new CommonResponse<>(studentGroupService.getStudentGroupById(id)));
         } catch (ServerException e) {
             log.error("Исключение ServerException в методе getStudentGroupById");
             return ResponseEntity
                     .status(HttpStatus.UNPROCESSABLE_CONTENT)
                     .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
         } catch (Exception e) {
             log.error("Исключение {} в методе getStudentGroupById", e.getClass());
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body(new CommonResponse<>(102, e.getMessage(), null));
         }
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<StudentGroupResponse>> addStudentGroup(@Valid @RequestBody StudentGroupRequest request) {
         log.info("Вызван метод addStudentGroup()");
         try {
             return ResponseEntity
                     .status(HttpStatus.CREATED)
                     .body(new CommonResponse<>(studentGroupService.addStudentGroup(request)));
         } catch (ServerException e) {
             log.error("Исключение ServerException в методе addStudentGroup");
             return ResponseEntity
                     .status(HttpStatus.UNPROCESSABLE_CONTENT)
                     .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
         }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentGroupResponse>> editStudentGroup(@PathVariable Long id,
            @Valid @RequestBody StudentGroupRequest request) {
        log.info("Вызван метод editStudentGroup({})", id);
        try {
            return ResponseEntity.ok(new CommonResponse<>(studentGroupService.editStudentGroup(id, request)));
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе editStudentGroup");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе editStudentGroup", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(104, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<CommonResponse<Void>> deleteStudentGroup(@PathVariable Long id) {
        log.info("Вызван метод deleteStudentGroup({})", id);
        try {
            studentGroupService.deleteStudentGroup(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе deleteStudentGroup");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(105, e.getMessage(), null));
        }
    }
}
