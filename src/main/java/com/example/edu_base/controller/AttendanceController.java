package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.service.attendance.IAttendanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("api/attendances")
public class AttendanceController {

    private final IAttendanceService attendanceService;

    public AttendanceController(IAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<AttendanceResponse>> addAttendance(@Valid @RequestBody AttendanceRequest request) {
        log.info("request to add attendance for lesson: {} ", request.getLessonId());

        AttendanceResponse response = attendanceService.addAttendance(request);

        log.info("attendance: {}, added successfully", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<AttendanceResponse>> getAttendanceById(@PathVariable @Min(1) long id) {
        log.info("request to get attendance by id: {}", id);

        AttendanceResponse response = attendanceService.getAttendanceById(id);

        log.info("attendance: {}, successfully received", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<List<AttendanceResponse>>> getAllAttendances() {
        log.info("request to get attendances");

        List<AttendanceResponse> response = attendanceService.getAttendances();

        log.info("attendances successfully received");

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<AttendanceResponse>> editAttendance(@PathVariable @Min(1) long id,
                                                                             @Valid @RequestBody AttendanceRequest request) {

        log.info("request to edit attendance by id: {}", id);

        AttendanceResponse response = attendanceService.editAttendance(id, request);

        log.info("attendance: {}, successfully edited", id);

        return ResponseEntity.ok(new CommonResponse<>(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CommonResponse<Void>> deleteAttendance(@PathVariable @Min(1) long id) {
        log.info("request to delete attendance by id: {}", id);

        attendanceService.deleteAttendance(id);

        log.info("attendance: {}, successfully deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}