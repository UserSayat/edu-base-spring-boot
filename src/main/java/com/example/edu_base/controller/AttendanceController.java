package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.service.attendance.IAttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//TODO Можно сохранять посещаемость в виде мапы (но это расточительно)
//TODO Можно в виде двух списков один id студентов другой список флагов был/не был (самый трудный вариант)
//TODO Можно сохранять как список пар (id студента и был/не был)
//TODO Редактировать можно наверное по отдельности (может даже через PATCH запрос)



@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final IAttendanceService attendanceService;

    public AttendanceController(IAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<AttendanceResponse>> addAttendance(@RequestBody AttendanceRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(attendanceService.addAttendance(request)));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<AttendanceResponse>> getAttendanceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(attendanceService.getAttendanceById(id)));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<AttendanceResponse>>> getAllAttendances() {
        try {
            return ResponseEntity.ok(new CommonResponse<>(attendanceService.getAllAttendances()));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<AttendanceResponse>> editAttendance(@PathVariable Long id,
                                                                             @RequestBody AttendanceRequest request) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(attendanceService.editAttendance(id, request)));
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteAttendance(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendance(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (ServerException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails()));
        }
    }
}
