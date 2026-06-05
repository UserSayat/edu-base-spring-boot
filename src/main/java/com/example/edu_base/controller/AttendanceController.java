package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.service.attendance.IAttendanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//TODO Можно сохранять посещаемость в виде мапы (но это расточительно)
//TODO Можно в виде двух списков один id студентов другой список флагов был/не был (самый трудный вариант)
//TODO Можно сохранять как список пар (id студента и был/не был)
//TODO Редактировать можно наверное по отдельности (может даже через PATCH запрос)



@RestController
@Validated
@RequestMapping("api/attendances")
public class AttendanceController {

    private final IAttendanceService attendanceService;

    public AttendanceController(IAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<AttendanceResponse>> addAttendance(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CommonResponse<>(attendanceService.addAttendance(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<AttendanceResponse>> getAttendanceById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(new CommonResponse<>(attendanceService.getAttendanceById(id)));
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<List<AttendanceResponse>>> getAllAttendances() {
        return ResponseEntity.ok(new CommonResponse<>(attendanceService.getAttendances()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<AttendanceResponse>> editAttendance(@PathVariable @Min(1) Long id,
                                                                             @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(new CommonResponse<>(attendanceService.editAttendance(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteAttendance(@PathVariable @Min(1) Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
