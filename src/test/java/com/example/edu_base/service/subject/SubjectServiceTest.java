package com.example.edu_base.service.subject;

import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Subject;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
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
class SubjectServiceTest {

    @Mock
    ISubjectRepository subjectRepository;
    @Mock
    ILessonRepository lessonRepository;

    @InjectMocks
    SubjectService subjectService;

    @Test
    void addSubject_returnsResponse() {
        SubjectRequest req = new SubjectRequest();
        req.setSubjectName("Math");

        Subject saved = new Subject(9L, "Math", ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(subjectRepository.save(any())).thenReturn(saved);

        SubjectResponse resp = subjectService.addSubject(req);

        assertEquals(9L, resp.getId());
        assertEquals("Math", resp.getSubjectName());
    }

    @Test
    void deleteSubject_throwsWhenLessonsExist() {
        when(lessonRepository.findBySubjectId(3L)).thenReturn(List.of(new Lesson()));

        assertThrows(IllegalArgumentException.class, () -> subjectService.deleteSubject(3L));
    }
}
