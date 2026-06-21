package com.example.edu_base.service.lesson;

import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.entity.Subject;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
import com.example.edu_base.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lesson Service Unit Tests")
class LessonServiceTest {

    @Mock
    private ILessonRepository lessonRepository;

    @Mock
    private IStudentGroupRepository studentGroupRepository;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private IAttendanceRepository attendanceRepository;

    @InjectMocks
    private LessonService lessonService;

    private LessonRequest validRequest;
    private Lesson lesson;
    private LessonResponse expectedResponse;
    private StudentGroup studentGroup;
    private Subject subject;
    private final long TEST_ID = 1L;
    private final long SUBJECT_ID = 100L;
    private final long STUDENT_GROUP_ID = 200L;
    private final long TEACHER_ID = 300L;
    private final int PAIR_NUMBER = 1;
    private final LocalDate DATE = LocalDate.of(2026, 6, 21);

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new LessonRequest();

        validRequest.setSubjectId(SUBJECT_ID);
        validRequest.setDate(DATE);
        validRequest.setPairNumber((long) PAIR_NUMBER);
        validRequest.setTeacherId(TEACHER_ID);
        validRequest.setStudentGroupId(STUDENT_GROUP_ID);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        lesson = new Lesson(
                TEST_ID,
                SUBJECT_ID,
                DATE,
                (long) PAIR_NUMBER,
                TEACHER_ID,
                STUDENT_GROUP_ID,
                now,
                now
        );

        expectedResponse = new LessonResponse(
                TEST_ID,
                SUBJECT_ID,
                DATE,
                (long) PAIR_NUMBER,
                TEACHER_ID,
                STUDENT_GROUP_ID,
                now,
                now
        );

        studentGroup = new StudentGroup(STUDENT_GROUP_ID, "Group A", now, now);
        subject = new Subject(SUBJECT_ID, "Mathematics", now, now);
    }


    //addLesson

    @Test
    @DisplayName("Should add lesson successfully when all validations pass")
    void addLesson_Success() throws ServerException {
        // Given
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        // When
        LessonResponse response = lessonService.addLesson(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(SUBJECT_ID, response.getSubjectId());
        assertEquals(STUDENT_GROUP_ID, response.getStudentGroupId());
        assertEquals(TEACHER_ID, response.getTeacherId());
        assertEquals(PAIR_NUMBER, response.getPairNumber());
        assertEquals(DATE, response.getDate());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(subjectRepository, times(1)).findById(SUBJECT_ID);
        verify(studentGroupRepository, times(1)).findById(STUDENT_GROUP_ID);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    @DisplayName("Should throw ServerException when subject not found")
    void addLesson_SubjectNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.addLesson(validRequest);
        });

        assertTrue(exception.getMessage().contains("subject with id: " + STUDENT_GROUP_ID + " not found"));
        assertEquals(5001, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(subjectRepository, times(1)).findById(SUBJECT_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found")
    void addLesson_StudentGroupNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.addLesson(validRequest);
        });

        assertTrue(exception.getMessage().contains("student group with id: " + STUDENT_GROUP_ID + " not found"));
        assertEquals(5001, exception.getErrorCode());

        verify(subjectRepository, times(1)).findById(SUBJECT_ID);
        verify(studentGroupRepository, times(1)).findById(STUDENT_GROUP_ID);
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addLesson_RepositorySaveFails_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.save(any(Lesson.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.addLesson(validRequest);
        });

        assertEquals(5001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(subjectRepository, times(1)).findById(SUBJECT_ID);
        verify(studentGroupRepository, times(1)).findById(STUDENT_GROUP_ID);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding lesson")
    void addLesson_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.save(any(Lesson.class)))
                .thenAnswer(invocation -> {
                    Lesson saved = invocation.getArgument(0);
                    return new Lesson(
                            TEST_ID,
                            saved.getSubjectId(),
                            saved.getDate(),
                            saved.getPairNumber(),
                            saved.getTeacherId(),
                            saved.getStudentGroupId(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        LessonResponse response = lessonService.addLesson(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        verify(lessonRepository).save(argThat(lesson ->
                lesson.getCreatedAt() != null &&
                        lesson.getUpdatedAt() != null &&
                        lesson.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }


    //getLessonById

    @Test
    @DisplayName("Should get lesson with attendance successfully")
    void getLessonById_Success() throws ServerException {
        // Given
        List<Long> studentIds = Arrays.asList(1L, 2L, 3L);

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findStudentsByLessonId(TEST_ID)).thenReturn(studentIds);

        // When
        LessonWithAttendanceResponse response = lessonService.getLessonById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(SUBJECT_ID, response.getSubjectId());
        assertEquals(STUDENT_GROUP_ID, response.getStudentGroupId());
        assertEquals(3, response.getAttendance().size());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(lessonRepository, times(1)).findStudentsByLessonId(TEST_ID);
        verify(attendanceRepository, times(1)).findByStudentId(1L);
        verify(attendanceRepository, times(1)).findByStudentId(2L);
        verify(attendanceRepository, times(1)).findByStudentId(3L);
    }

    @Test
    @DisplayName("Should get lesson with empty attendance when no students")
    void getLessonById_NoStudents_ReturnsEmptyAttendance() throws ServerException {
        // Given
        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findStudentsByLessonId(TEST_ID)).thenReturn(List.of());

        // When
        LessonWithAttendanceResponse response = lessonService.getLessonById(TEST_ID);

        // Then
        assertNotNull(response);
        assertTrue(response.getAttendance().isEmpty());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(lessonRepository, times(1)).findStudentsByLessonId(TEST_ID);
        verify(attendanceRepository, never()).findByStudentId(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when lesson not found")
    void getLessonById_LessonNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.getLessonById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("lesson: " + TEST_ID + " not found"));
        assertEquals(5002, exception.getErrorCode());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(lessonRepository, never()).findStudentsByLessonId(anyLong());
        verify(attendanceRepository, never()).findByStudentId(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when finding students fails")
    void getLessonById_FindStudentsFails_ThrowsServerException() throws ServerException {
        // Given
        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findStudentsByLessonId(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.getLessonById(TEST_ID);
        });

        assertEquals(5002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(lessonRepository, times(1)).findStudentsByLessonId(TEST_ID);
        verify(attendanceRepository, never()).findByStudentId(anyLong());
    }

    @Test
    @DisplayName("Should handle exception when checking attendance")
    void getLessonById_AttendanceCheckFails_ThrowsServerException() throws ServerException {
        // Given
        List<Long> studentIds = Arrays.asList(1L, 2L);

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findStudentsByLessonId(TEST_ID)).thenReturn(studentIds);
        when(attendanceRepository.findByStudentId(1L))
                .thenThrow(new RuntimeException("Attendance check failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.getLessonById(TEST_ID);
        });

        assertEquals(5002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Attendance check failed"));

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(lessonRepository, times(1)).findStudentsByLessonId(TEST_ID);
        verify(attendanceRepository, times(1)).findByStudentId(1L);
    }


    //getLessons

    @Test
    @DisplayName("Should get all lessons successfully")
    void getLessons_Success() throws ServerException {
        // Given
        List<Lesson> lessons = Arrays.asList(
                new Lesson(1L, 101L, DATE, 1L, 301L, 201L,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC)),
                new Lesson(2L, 102L, DATE, 2L, 302L, 202L,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))
        );

        when(lessonRepository.findAll()).thenReturn(lessons);

        // When
        List<LessonResponse> responses = lessonService.getLessons();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals(101L, responses.get(0).getSubjectId());
        assertEquals(102L, responses.get(1).getSubjectId());

        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no lessons exist")
    void getLessons_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(lessonRepository.findAll()).thenReturn(List.of());

        // When
        List<LessonResponse> responses = lessonService.getLessons();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getLessons_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(lessonRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.getLessons();
        });

        assertEquals(5003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database connection failed"));

        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle null from repository")
    void getLessons_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(lessonRepository.findAll()).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.getLessons();
        });

        assertEquals(5003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(lessonRepository, times(1)).findAll();
    }


    //editLesson

    @Test
    @DisplayName("Should edit lesson successfully when valid")
    void editLesson_Success() throws ServerException {
        // Given
        LessonRequest editRequest = new LessonRequest();

        editRequest.setSubjectId(999L);
        editRequest.setDate(LocalDate.of(2026, 6, 22));
        editRequest.setPairNumber(2L);
        editRequest.setTeacherId(888L);
        editRequest.setStudentGroupId(777L);

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(studentGroupRepository.findById(777L)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.findByDateAndPairNumber(editRequest.getDate(), editRequest.getPairNumber()))
                .thenReturn(Optional.empty());
        when(lessonRepository.update(any(Lesson.class))).thenReturn(true);

        // When
        LessonResponse response = lessonService.editLesson(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(999L, response.getSubjectId());
        assertEquals(777L, response.getStudentGroupId());
        assertEquals(888L, response.getTeacherId());
        assertEquals(2, response.getPairNumber());
        assertEquals(LocalDate.of(2026, 6, 22), response.getDate());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(777L);
        verify(lessonRepository, times(1)).findByDateAndPairNumber(
                editRequest.getDate(), editRequest.getPairNumber()
        );
        verify(lessonRepository, times(1)).update(any(Lesson.class));
    }

    @Test
    @DisplayName("Should throw ServerException when lesson not found")
    void editLesson_LessonNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.editLesson(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("lesson: " + TEST_ID + " not found"));
        assertEquals(5004, exception.getErrorCode());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
        verify(lessonRepository, never()).findByDateAndPairNumber(any(), anyInt());
        verify(lessonRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found")
    void editLesson_StudentGroupNotFound_ThrowsServerException() throws ServerException {
        // Given
        LessonRequest editRequest = new LessonRequest();

        editRequest.setSubjectId(SUBJECT_ID);
        editRequest.setDate(DATE);
        editRequest.setPairNumber((long) PAIR_NUMBER);
        editRequest.setTeacherId(TEACHER_ID);
        editRequest.setStudentGroupId(999L);

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(studentGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.editLesson(TEST_ID, editRequest);
        });

        assertTrue(exception.getMessage().contains("student group: " + TEST_ID + " not found"));
        assertEquals(5004, exception.getErrorCode());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(999L);
        verify(lessonRepository, never()).findByDateAndPairNumber(any(), anyInt());
        verify(lessonRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when time slot is busy")
    void editLesson_TimeSlotBusy_ThrowsServerException() throws ServerException {
        // Given
        LessonRequest editRequest = new LessonRequest();

        editRequest.setSubjectId(SUBJECT_ID);
        editRequest.setDate(LocalDate.of(2026, 6, 22));
        editRequest.setPairNumber(2L);
        editRequest.setTeacherId(TEACHER_ID);
        editRequest.setStudentGroupId(STUDENT_GROUP_ID);

        Lesson existingLesson = new Lesson(
                2L, SUBJECT_ID,
                LocalDate.of(2026, 6, 22),
                2L,
                TEACHER_ID,
                STUDENT_GROUP_ID,
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.findByDateAndPairNumber(
                editRequest.getDate(), editRequest.getPairNumber()
        )).thenReturn(Optional.of(existingLesson));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            lessonService.editLesson(TEST_ID, editRequest);
        });

        assertTrue(exception.getMessage().contains("can not edit lesson, time is busy already"));
        assertEquals(5004, exception.getErrorCode());

        verify(lessonRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(STUDENT_GROUP_ID);
        verify(lessonRepository, times(1)).findByDateAndPairNumber(
                editRequest.getDate(), editRequest.getPairNumber()
        );
        verify(lessonRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should update timestamp when editing lesson")
    void editLesson_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        lesson.setCreatedAt(oldTimestamp);
        lesson.setUpdatedAt(oldTimestamp);

        when(lessonRepository.findById(TEST_ID)).thenReturn(Optional.of(lesson));
        when(studentGroupRepository.findById(STUDENT_GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(lessonRepository.findByDateAndPairNumber(DATE, PAIR_NUMBER)).thenReturn(Optional.empty());
        when(lessonRepository.update(any(Lesson.class))).thenReturn(true);

        // When
        lessonService.editLesson(TEST_ID, validRequest);

        // Then
        verify(lessonRepository).update(argThat(lesson ->
                lesson.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !lesson.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        lesson.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }


    //deleteLesson

    @Test
    @DisplayName("Should delete lesson successfully when exists")
    void deleteLesson_Success() throws ServerException {
        // Given
        when(lessonRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        lessonService.deleteLesson(TEST_ID);

        // Then
        verify(lessonRepository, times(1)).deleteById(TEST_ID);
    }
}