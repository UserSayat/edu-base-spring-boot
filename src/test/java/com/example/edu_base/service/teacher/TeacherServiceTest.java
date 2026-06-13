package com.example.edu_base.service.teacher;

import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Teacher;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.teacher.ITeacherRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    ITeacherRepository teacherRepository;
    @Mock
    ILessonRepository lessonRepository;

    @InjectMocks
    TeacherService teacherService;

    @Test
    void addTeacher_returnsResponse() {
        TeacherRequest req = new TeacherRequest();
        req.setLastName("Petrov");
        req.setFirstName("Petr");
        req.setMiddleName("P.");

        Teacher saved = new Teacher(15L, "Petrov", "Petr", "P.", ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(teacherRepository.save(any())).thenReturn(saved);

        TeacherResponse resp = teacherService.addTeacher(req);

        assertEquals(15L, resp.getId());
        assertEquals("Petrov", resp.getLastName());
    }

    @Test
    void deleteTeacher_throwsWhenLessonsExist() {
        when(lessonRepository.findByTeacherId(4L)).thenReturn(List.of(new Lesson()));

        assertThrows(IllegalArgumentException.class, () -> teacherService.deleteTeacher(4L));
    }
}
