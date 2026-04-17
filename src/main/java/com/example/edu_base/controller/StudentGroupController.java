package com.example.edu_base.controller;

import com.example.edu_base.dto.StudentGroupDto;
import com.example.edu_base.service.StudentGroupService;
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

    @GetMapping("/getStudentGroups")
    public ResponseEntity<List<StudentGroupDto>> getStudentGroups() {
         log.info("Вызван метод getStudentGroups()");
         return ResponseEntity.ok(studentGroupService.getStudentGroups());
    }

    @GetMapping("/getStudentGroupById/{id}")
    public ResponseEntity<StudentGroupDto> getStudentGroupById(@PathVariable Long id) {
         log.info("Вызван метод getStudentGroupById({})", id);
         return ResponseEntity.ok(studentGroupService.getStudentGroupById(id));
    }

    @PostMapping("/addStudentGroup")
    public ResponseEntity<StudentGroupDto> addStudentGroup(@RequestBody StudentGroupDto studentGroupDto) {
         log.info("Вызван метод addStudentGroup()");
         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(studentGroupService.addStudentGroup(studentGroupDto));
    }

    @PutMapping("/editStudentGroup/{id}")
    public ResponseEntity<StudentGroupDto> editStudentGroup(@PathVariable Long id,
            @RequestBody StudentGroupDto studentGroupDto) {
        log.info("Вызван метод editStudentGroup({})", id);
         return ResponseEntity.ok(studentGroupService.editStudentGroup(id, studentGroupDto));
    }

    @DeleteMapping("/deleteStudentGroup/{id}")
    private ResponseEntity<Void> deleteStudentGroup(@PathVariable Long id) {
        log.info("Вызван метод deleteStudentGroup({})", id);
         studentGroupService.deleteStudentGroup(id);
         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
