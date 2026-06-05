package com.example.edu_base.service.attendance;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;

import java.util.List;

public interface IAttendanceService {
    AttendanceResponse addAttendance(AttendanceRequest request) throws ServerException;
    AttendanceResponse getAttendanceById(Long id) throws ServerException;
    List<AttendanceResponse> getAttendances() throws ServerException;
    AttendanceResponse editAttendance(Long id, AttendanceRequest request) throws ServerException;
    void deleteAttendance(Long id) throws ServerException;
}
