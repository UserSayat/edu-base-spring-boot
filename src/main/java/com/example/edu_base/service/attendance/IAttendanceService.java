package com.example.edu_base.service.attendance;

import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;

import java.util.List;

public interface IAttendanceService {
    AttendanceResponse addAttendance(AttendanceRequest request);
    AttendanceResponse getAttendanceById(long id);
    List<AttendanceResponse> getAttendances();
    AttendanceResponse editAttendance(long id, AttendanceRequest request);
    void deleteAttendance(long id);
}
