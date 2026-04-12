package com.example.edu_base.controller;

import com.example.edu_base.dto.StudentGroupDto;
import com.example.edu_base.service.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class StudentGroupController {
    @Autowired
    private final StudentGroupService studentGroupService;

     public StudentGroupController(StudentGroupService studentGroupService) {
         this.studentGroupService = studentGroupService;
     }

    @GetMapping("/getStudentGroups")
    public ResponseEntity<List<StudentGroupDto>> getStudentGroups() {
        return ResponseEntity.ok(studentGroupService.getStudentGroups());
    }

    @GetMapping("/getStudentGroupById/{id}")
    public ResponseEntity<StudentGroupDto> getStudentGroupById(@PathVariable Long id) {
         return ResponseEntity.ok(studentGroupService.getStudentGroupById(id));
    }

    @PostMapping("/addStudentGroup")
    public ResponseEntity<StudentGroupDto> addStudentGroup(@RequestBody StudentGroupDto studentGroupDto) {
         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(studentGroupService.addStudentGroup(studentGroupDto));
    }

    @PutMapping("/editStudentGroup/{id}")
    public ResponseEntity<StudentGroupDto> editStudentGroup(@PathVariable Long id,
            @RequestBody StudentGroupDto studentGroupDto) {
         return ResponseEntity.ok(studentGroupService.editStudentGroup(id, studentGroupDto));
    }

    @DeleteMapping("/deletStudentGroup/{id}")
    private ResponseEntity<Void> deleteStudentGroup(@PathVariable Long id) {
         studentGroupService.deleteStudentGroup(id);
         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
