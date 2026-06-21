package com.example.edu_base.service.teacher;

import com.example.edu_base.dto.teacher.TeacherRequest;
import com.example.edu_base.dto.teacher.TeacherResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Teacher;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.exception.ServerException;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.teacher.ITeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teacher Service Unit Tests")
class TeacherServiceTest {

    @Mock
    private ITeacherRepository teacherRepository;

    @Mock
    private ILessonRepository lessonRepository;

    @InjectMocks
    private TeacherService teacherService;

    private TeacherRequest validRequest;
    private Teacher teacher;
    private TeacherResponse expectedResponse;

    private final long TEST_ID = 1L;
    private final String LAST_NAME = "Ivanov";
    private final String FIRST_NAME = "Ivan";
    private final String MIDDLE_NAME = "Ivanovich";

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new TeacherRequest();

        validRequest.setLastName(LAST_NAME);
        validRequest.setFirstName(FIRST_NAME);
        validRequest.setMiddleName(MIDDLE_NAME);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        teacher = new Teacher(
                TEST_ID,
                LAST_NAME,
                FIRST_NAME,
                MIDDLE_NAME,
                now,
                now
        );

        expectedResponse = new TeacherResponse(
                TEST_ID,
                LAST_NAME,
                FIRST_NAME,
                MIDDLE_NAME,
                now,
                now
        );
    }


    //addTeacher

    @Test
    @DisplayName("Should add teacher successfully when valid")
    void addTeacher_Success() throws ServerException {
        // Given
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        // When
        TeacherResponse response = teacherService.addTeacher(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LAST_NAME, response.getLastName());
        assertEquals(FIRST_NAME, response.getFirstName());
        assertEquals(MIDDLE_NAME, response.getMiddleName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addTeacher_RepositorySaveFails_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.save(any(Teacher.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.addTeacher(validRequest);
        });

        assertEquals(3001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should throw ServerException with generic message when exception has null message")
    void addTeacher_ExceptionWithNullMessage_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException exception = new RuntimeException();
        when(teacherRepository.save(any(Teacher.class))).thenThrow(exception);

        // When & Then
        ServerException serverException = assertThrows(ServerException.class, () -> {
            teacherService.addTeacher(validRequest);
        });

        assertEquals("java.lang.RuntimeException", serverException.getMessage());
        assertEquals(3001, serverException.getErrorCode());
        assertSame(exception, serverException.getCause());

        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding teacher")
    void addTeacher_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(teacherRepository.save(any(Teacher.class)))
                .thenAnswer(invocation -> {
                    Teacher saved = invocation.getArgument(0);
                    return new Teacher(
                            TEST_ID,
                            saved.getLastName(),
                            saved.getFirstName(),
                            saved.getMiddleName(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        TeacherResponse response = teacherService.addTeacher(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        verify(teacherRepository).save(argThat(teacher ->
                teacher.getCreatedAt() != null &&
                        teacher.getUpdatedAt() != null &&
                        teacher.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void addTeacher_NullRequest_ThrowsException() throws ServerException {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            teacherService.addTeacher(null);
        });

        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle empty names gracefully")
    void addTeacher_EmptyNames_AddsSuccessfully() throws ServerException {
        // Given
        TeacherRequest request = new TeacherRequest();

        request.setLastName("");
        request.setFirstName("");
        request.setMiddleName("");

        Teacher teacherWithEmptyNames = new Teacher(
                TEST_ID,
                "",
                "",
                "",
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacherWithEmptyNames);

        // When
        TeacherResponse response = teacherService.addTeacher(request);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals("", response.getLastName());
        assertEquals("", response.getFirstName());
        assertEquals("", response.getMiddleName());

        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }


    //getTeacherById

    @Test
    @DisplayName("Should get teacher by id successfully when exists")
    void getTeacherById_Success() throws ServerException {
        // Given
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(teacher));

        // When
        TeacherResponse response = teacherService.getTeacherById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LAST_NAME, response.getLastName());
        assertEquals(FIRST_NAME, response.getFirstName());
        assertEquals(MIDDLE_NAME, response.getMiddleName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(teacherRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when teacher not found")
    void getTeacherById_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.getTeacherById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("teacher: " + TEST_ID + " not found"));
        assertEquals(3002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(teacherRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getTeacherById_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.findById(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.getTeacherById(TEST_ID);
        });

        assertEquals(3002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(teacherRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should handle invalid id gracefully")
    void getTeacherById_InvalidId_ThrowsServerException() throws ServerException {
        // Given
        long invalidId = -1L;
        when(teacherRepository.findById(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid id"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.getTeacherById(invalidId);
        });

        assertEquals(3002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid id"));

        verify(teacherRepository, times(1)).findById(invalidId);
    }


    //getTeachers

    @Test
    @DisplayName("Should get all teachers successfully")
    void getTeachers_Success() throws ServerException {
        // Given
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Teacher> teachers = Arrays.asList(
                new Teacher(1L, "Ivanov", "Ivan", "Ivanovich", now, now),
                new Teacher(2L, "Petrov", "Petr", "Petrovich", now, now),
                new Teacher(3L, "Sidorov", "Sidor", "Sidorovich", now, now)
        );

        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<TeacherResponse> responses = teacherService.getTeachers();

        // Then
        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Ivanov", responses.get(0).getLastName());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("Petrov", responses.get(1).getLastName());
        assertEquals(3L, responses.get(2).getId());
        assertEquals("Sidorov", responses.get(2).getLastName());

        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no teachers exist")
    void getTeachers_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(teacherRepository.findAll()).thenReturn(List.of());

        // When
        List<TeacherResponse> responses = teacherService.getTeachers();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getTeachers_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.getTeachers();
        });

        assertEquals(3003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database connection failed"));

        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle null from repository")
    void getTeachers_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.findAll()).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.getTeachers();
        });

        assertEquals(3003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(teacherRepository, times(1)).findAll();
    }


    //editTeacher

    @Test
    @DisplayName("Should edit teacher successfully when valid")
    void editTeacher_Success() throws ServerException {
        // Given
        TeacherRequest editRequest = new TeacherRequest();

        editRequest.setLastName("Petrova");
        editRequest.setFirstName("Anna");
        editRequest.setMiddleName("Sergeevna");

        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(teacher));
        when(teacherRepository.update(any(Teacher.class))).thenReturn(true);

        // When
        TeacherResponse response = teacherService.editTeacher(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals("Petrova", response.getLastName());
        assertEquals("Anna", response.getFirstName());
        assertEquals("Sergeevna", response.getMiddleName());

        verify(teacherRepository, times(1)).findById(TEST_ID);
        verify(teacherRepository, times(1)).update(argThat(teacher ->
                teacher.getLastName().equals("Petrova") &&
                        teacher.getFirstName().equals("Anna") &&
                        teacher.getMiddleName().equals("Sergeevna") &&
                        teacher.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when teacher not found")
    void editTeacher_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.editTeacher(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("teacher: " + TEST_ID + " not found"));
        assertEquals(3004, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(teacherRepository, times(1)).findById(TEST_ID);
        verify(teacherRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should update timestamp when editing teacher")
    void editTeacher_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        teacher.setCreatedAt(oldTimestamp);
        teacher.setUpdatedAt(oldTimestamp);

        TeacherRequest editRequest = new TeacherRequest();

        editRequest.setLastName("NewLastName");
        editRequest.setFirstName("NewFirstName");
        editRequest.setMiddleName("NewMiddleName");

        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(teacher));
        when(teacherRepository.update(any(Teacher.class))).thenReturn(true);

        // When
        teacherService.editTeacher(TEST_ID, editRequest);

        // Then
        verify(teacherRepository).update(argThat(teacher ->
                teacher.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !teacher.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        teacher.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when repository update fails")
    void editTeacher_UpdateFails_ThrowsServerException() throws ServerException {
        // Given
        TeacherRequest editRequest = new TeacherRequest();

        editRequest.setLastName("NewLastName");
        editRequest.setFirstName("NewFirstName");
        editRequest.setMiddleName("NewMiddleName");

        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(teacher));
        when(teacherRepository.update(any(Teacher.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            teacherService.editTeacher(TEST_ID, editRequest);
        });

        assertEquals(3004, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Update failed"));

        verify(teacherRepository, times(1)).findById(TEST_ID);
        verify(teacherRepository, times(1)).update(any(Teacher.class));
    }


    //deleteTeacher

    @Test
    @DisplayName("Should delete teacher successfully when no lessons exist")
    void deleteTeacher_Success() throws ServerException {
        // Given
        when(lessonRepository.findByTeacherId(TEST_ID)).thenReturn(List.of());
        when(teacherRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        teacherService.deleteTeacher(TEST_ID);

        // Then
        verify(lessonRepository, times(1)).findByTeacherId(TEST_ID);
        verify(teacherRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when teacher has lessons")
    void deleteTeacher_TeacherHasLessons_ThrowsIllegalArgumentException() throws ServerException {
        // Given
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Lesson> lessons = Arrays.asList(
                new Lesson(1L, 100L, LocalDate.now(), 1L, TEST_ID, 200L, now, now),
                new Lesson(2L, 100L, LocalDate.now(), 2L, TEST_ID, 200L, now, now)
        );

        when(lessonRepository.findByTeacherId(TEST_ID)).thenReturn(lessons);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            teacherService.deleteTeacher(TEST_ID);
        });

        assertEquals("can not delete teacher while lessons with him exist", exception.getMessage());

        verify(lessonRepository, times(1)).findByTeacherId(TEST_ID);
        verify(teacherRepository, never()).deleteById(anyLong());
    }
}