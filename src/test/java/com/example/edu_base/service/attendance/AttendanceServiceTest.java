package com.example.edu_base.service.attendance;

import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    IAttendanceRepository attendanceRepository;

    @InjectMocks
    AttendanceService attendanceService;

    @Test
    void addAttendance_returnsResponse() {
        AttendanceRequest req = new AttendanceRequest();
        req.setLessonId(10L);
        req.setStudentId(20L);
        req.setPresent(true);

        Attendance saved = new Attendance(1L, 10L, 20L, true, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(attendanceRepository.save(any())).thenReturn(saved);

        AttendanceResponse resp = attendanceService.addAttendance(req);

        assertEquals(1L, resp.getId());
        assertEquals(10L, resp.getLessonId());
        assertTrue(resp.isPresent());
    }

    @Test
    void getAttendanceById_returnsResponse() {
        Attendance attendance = new Attendance(2L, 11L, 22L, false, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(attendanceRepository.findById(2L)).thenReturn(Optional.of(attendance));

        AttendanceResponse resp = attendanceService.getAttendanceById(2L);

        assertEquals(2L, resp.getId());
        assertEquals(11L, resp.getLessonId());
        assertFalse(resp.isPresent());
    }

    @Test
    void getAttendances_returnsList() {
        Attendance a = new Attendance(3L, 12L, 23L, true, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(attendanceRepository.findAll()).thenReturn(List.of(a));

        List<AttendanceResponse> list = attendanceService.getAttendances();

        assertEquals(1, list.size());
        assertEquals(3L, list.get(0).getId());
    }

    @Test
    void editAttendance_updatesAndReturns() {
        Attendance existing = new Attendance(4L, 13L, 24L, false, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(attendanceRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(attendanceRepository.update(eq(4L), any())).thenReturn(true);

        AttendanceRequest req = new AttendanceRequest();
        req.setLessonId(14L);
        req.setStudentId(25L);
        req.setPresent(true);

        AttendanceResponse resp = attendanceService.editAttendance(4L, req);

        assertEquals(4L, resp.getId());
        assertEquals(14L, resp.getLessonId());
        assertTrue(resp.isPresent());
    }

    @Test
    void deleteAttendance_throwsWhenNotDeleted() {
        when(attendanceRepository.deleteById(5L)).thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class, () -> attendanceService.deleteAttendance(5L));
        assertTrue(ex.getMessage().contains("attendance wasn't delete") || ex instanceof com.example.edu_base.exception.ServerException);
    }
}
