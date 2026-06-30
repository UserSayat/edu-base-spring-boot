package com.example.edu_base.service.attendance;

import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Student;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository attendanceRepository;
    private final IStudentRepository studentRepository;
    private final ILessonRepository lessonRepository;

    public AttendanceService(IAttendanceRepository attendanceRepository, IStudentRepository studentRepository, ILessonRepository lessonRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public AttendanceResponse addAttendance(AttendanceRequest request) {
        log.info("adding attendance for lesson: {}", request.getLessonId());

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("student " + request.getStudentId() + " not found"));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lesson " + request.getLessonId() + " not found"));

        if (!student.getStudentGroupId().equals(lesson.getStudentGroupId()))
            throw new IllegalArgumentException("group number does not match");

        Attendance attendance = new Attendance(null,
                request.getLessonId(),
                request.getStudentId(),
                request.isPresent(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));

        return toAttendanceResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponse getAttendanceById(long id) {
        log.info("getting attendance by id: {}", id);
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("attendance: " + id + " not found"));

        return toAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAttendances() {
        log.info("getting all attendances");
        return attendanceRepository.findAll().stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    @Override
    public AttendanceResponse editAttendance(long id, AttendanceRequest request) {
        log.info("editing attendance by id: {}", id);
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("attendance: " + id + " not found"));

        attendance.setLessonId(request.getLessonId());
        attendance.setStudentId(request.getStudentId());
        attendance.setPresent(request.isPresent());
        attendance.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

        attendanceRepository.update(id, attendance);

        return toAttendanceResponse(attendance);
    }

    @Override
    public void deleteAttendance(long id) {
        log.info("deleting attendance by id: {}", id);

        attendanceRepository.deleteById(id);
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
