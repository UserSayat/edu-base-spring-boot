package com.example.edu_base.service.attendance;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository attendanceRepository;

    public AttendanceService(IAttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public AttendanceResponse addAttendance(AttendanceRequest request) throws ServerException {
        if (request.getId() != null) {
            throw new IllegalArgumentException("id should be null!");
        }

        try {
            Attendance attendance = new Attendance(null,
                    request.getLessonId(),
                    request.getStudentId(),
                    request.isPresent(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));

            return toAttendanceResponse(attendanceRepository.save(attendance));
        } catch (Exception e) {
            throw new ServerException(e.getCause().toString(), e, 203, null);
        }
    }

    @Override
    public AttendanceResponse getAttendanceById(Long id) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }

        try {
            Attendance attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("attendance: " + id + " not found"));
            return toAttendanceResponse(attendance);
        } catch (Exception e) {
            throw new ServerException("db error: getAttendanceById", e, 202, null);
        }
    }

    @Override
    public List<AttendanceResponse> getAllAttendances() throws ServerException {
        try {
            return attendanceRepository.findAll().stream()
                    .map(this::toAttendanceResponse)
                    .toList();
        } catch (Exception e) {
            throw new ServerException("db error: getAttendances", e, 101, null);
        }
    }

    @Override
    public AttendanceResponse editAttendance(Long id, AttendanceRequest request) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }

        try {
            Attendance attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("attendance: " + id + " not found"));

            attendance.setLessonId(request.getLessonId());
            attendance.setStudentId(request.getStudentId());
            attendance.setPresent(request.isPresent());
            attendance.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            attendanceRepository.update(id, attendance);

            return toAttendanceResponse(attendance);
        } catch (Exception e) {
            throw new ServerException("db error: editAttendance()", e, 204, null);
        }
    }

    @Override
    public void deleteAttendance(Long id) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }
        boolean deleted = attendanceRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("Attendance wasn't delete", 105, null);
    }

    public AttendanceResponse toAttendanceResponse(Attendance attendance) {
        return new AttendanceResponse(attendance.getId(),
                attendance.getLessonId(),
                attendance.getStudentId(),
                attendance.isPresent(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt());
    }
}
