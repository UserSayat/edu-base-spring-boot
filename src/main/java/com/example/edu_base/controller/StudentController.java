package com.example.edu_base.controller;

import java.util.List;
import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.service.student.IStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/student")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<CommonResponse<List<StudentResponse>>> getStudentsByGroup(@PathVariable Long id) {
        log.info("Вызван метод getStudentsByGroup({})", id);
        try {
            return ResponseEntity.ok(new CommonResponse<>(studentService.getStudentsByGroup(id)));
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе getStudentsByGroup");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе getStudentsByGroup", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(201, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        log.info("Вызван метод getStudentById({})", id);
        try {
            return ResponseEntity.ok(new CommonResponse<>(studentService.getStudentById(id)));
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе getStudentById");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе getStudentById", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(202, e.getMessage(), null));
        }
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<StudentResponse>> addStudent(@RequestBody StudentRequest request) {
        log.info("Вызван метод addStudent()");
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(studentService.addStudent(request)));
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе addStudent");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе addStudent", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(203, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<StudentResponse>> editStudent(@PathVariable Long id,
                                                                       @RequestBody StudentRequest request) {
        log.info("Вызван метод editStudent()");
        try {
            return ResponseEntity.ok(new CommonResponse<>(studentService.editStudent(id, request)));
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе editStudent");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе editStudent", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(204, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteStudent(@PathVariable Long id) {
        log.info("Вызван метод deleteStudent()");
        try {
            studentService.deleteStudent(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (ServerException e) {
            log.error("Исключение ServerException в методе deleteStudent");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        } catch (Exception e) {
            log.error("Исключение {} в методе deleteStudent", e.getClass());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse<>(205, e.getMessage(), null));
        }
    }
}
