package com.example.edu_base.service.attendance;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;

import java.util.List;

public interface IAttendanceService {
    AttendanceResponse addAttendance(AttendanceRequest request) throws ServerException;
    AttendanceResponse getAttendanceById(long id) throws ServerException;
    List<AttendanceResponse> getAttendances() throws ServerException;
    AttendanceResponse editAttendance(long id, AttendanceRequest request) throws ServerException;
    void deleteAttendance(long id) throws ServerException;
}
