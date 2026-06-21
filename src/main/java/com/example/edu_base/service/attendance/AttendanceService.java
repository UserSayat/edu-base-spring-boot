package com.example.edu_base.service.attendance;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository attendanceRepository;

    public AttendanceService(IAttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public AttendanceResponse addAttendance(AttendanceRequest request) throws ServerException {
        log.info("adding attendance for lesson: {}", request.getLessonId());
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
            log.error("failed to add attendance for lesson: {}",
                    request.getLessonId(), e);
            throw new ServerException(message, e, 6001, null);
        }
    }

    @Override
    public AttendanceResponse getAttendanceById(long id) throws ServerException {
        log.info("getting attendance by id: {}", id);
        try {
            Attendance attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("attendance: " + id + " not found"));

            return toAttendanceResponse(attendance);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get attendance: {}", id, e);
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
            log.error("failed to get attendances", e);
            throw new ServerException(message, e, 6003, null);
        }
    }

    @Override
    public AttendanceResponse editAttendance(long id, AttendanceRequest request) throws ServerException {
        log.info("editing attendance by id: {}", id);
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
            log.error("failed to edit attendance: {}", id, e);
            throw new ServerException(message, e, 6004, null);
        }
    }

    @Override
    public void deleteAttendance(long id) throws ServerException {
        log.info("deleting attendance by id: {}", id);
        try {
            attendanceRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServerException("attendance wasn't delete", e, 6005, null);
        }
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
