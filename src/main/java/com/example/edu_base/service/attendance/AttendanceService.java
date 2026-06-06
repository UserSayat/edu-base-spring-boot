package com.example.edu_base.service.attendance;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import jakarta.persistence.EntityNotFoundException;
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
        try {
            Attendance attendance = new Attendance(null,
                    request.getLessonId(),
                    request.getStudentId(),
                    request.isPresent(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));

            return toAttendanceResponse(attendanceRepository.save(attendance));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 6001, null);
        }
    }

    @Override
    public AttendanceResponse getAttendanceById(long id) throws ServerException {
        try {
            Attendance attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("attendance: " + id + " not found"));
            return toAttendanceResponse(attendance);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 6002, null);
        }
    }

    @Override
    public List<AttendanceResponse> getAttendances() throws ServerException {
        try {
            return attendanceRepository.findAll().stream()
                    .map(this::toAttendanceResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 6003, null);
        }
    }

    @Override
    public AttendanceResponse editAttendance(long id, AttendanceRequest request) throws ServerException {
        try {
            Attendance attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("attendance: " + id + " not found"));

            attendance.setLessonId(request.getLessonId());
            attendance.setStudentId(request.getStudentId());
            attendance.setPresent(request.isPresent());
            attendance.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            attendanceRepository.update(id, attendance);

            return toAttendanceResponse(attendance);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 6004, null);
        }
    }

    @Override
    public void deleteAttendance(long id) throws ServerException {

        boolean deleted = attendanceRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("attendance wasn't delete", 6005, null);
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
