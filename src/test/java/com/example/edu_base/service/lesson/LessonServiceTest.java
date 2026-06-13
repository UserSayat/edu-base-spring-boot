package com.example.edu_base.service.lesson;

import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    ILessonRepository lessonRepository;
    @Mock
    IStudentGroupRepository studentGroupRepository;
    @Mock
    ISubjectRepository subjectRepository;
    @Mock
    IAttendanceRepository attendanceRepository;

    @InjectMocks
    LessonService lessonService;

    @Test
    void addLesson_returnsResponse() {
        LessonRequest req = new LessonRequest();
        req.setSubjectId(1L);
        req.setStudentGroupId(2L);
        req.setDate(LocalDate.now());
        req.setPairNumber(1L);
        req.setTeacherId(3L);

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(new com.example.edu_base.entity.Subject()));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(new com.example.edu_base.entity.StudentGroup()));
        Lesson saved = new Lesson(10L, 1L, req.getDate(), req.getPairNumber(), 3L, 2L, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(lessonRepository.save(any())).thenReturn(saved);

        LessonResponse resp = lessonService.addLesson(req);

        assertEquals(10L, resp.getId());
        assertEquals(1L, resp.getSubjectId());
    }

    @Test
    void getLessonById_returnsWithAttendance() {
        Lesson lesson = new Lesson(20L, 2L, LocalDate.now(), 2L, 4L, 5L, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(lessonRepository.findById(20L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findStudentsByLessonId(20L)).thenReturn(List.of(100L));
        when(attendanceRepository.findByStudentId(100L)).thenReturn(Optional.of(new com.example.edu_base.entity.Attendance()));

        LessonWithAttendanceResponse resp = lessonService.getLessonById(20L);

        assertEquals(20L, resp.getId());
        assertEquals(1, resp.getAttendance().size());
        assertTrue(resp.getAttendance().get(0).getRight());
    }

    @Test
    void getLessons_returnsList() {
        Lesson l = new Lesson(30L, 3L, LocalDate.now(), 1L, 6L, 7L, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(lessonRepository.findAll()).thenReturn(List.of(l));

        List<LessonResponse> list = lessonService.getLessons();

        assertEquals(1, list.size());
        assertEquals(30L, list.get(0).getId());
    }
}
