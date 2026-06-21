package com.example.edu_base.service.attendance;

import com.example.edu_base.dto.attendance.AttendanceRequest;
import com.example.edu_base.dto.attendance.AttendanceResponse;
import com.example.edu_base.entity.Attendance;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Attendance Service Unit Tests")
class AttendanceServiceTest {

    @Mock
    private IAttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private AttendanceRequest validRequest;
    private Attendance attendance;
    private AttendanceResponse expectedResponse;
    private final long TEST_ID = 1L;
    private final long LESSON_ID = 100L;
    private final long STUDENT_ID = 200L;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new AttendanceRequest();

        validRequest.setLessonId(LESSON_ID);
        validRequest.setStudentId(STUDENT_ID);
        validRequest.setPresent(true);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        attendance = new Attendance(
                TEST_ID,
                LESSON_ID,
                STUDENT_ID,
                true,
                now,
                now
        );

        expectedResponse = new AttendanceResponse(
                TEST_ID,
                LESSON_ID,
                STUDENT_ID,
                true,
                now,
                now
        );
    }


    //addAttendance

    @Test
    @DisplayName("Should add attendance successfully with valid request")
    void addAttendance_Success() throws ServerException {
        // Given
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        // When
        AttendanceResponse response = attendanceService.addAttendance(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LESSON_ID, response.getLessonId());
        assertEquals(STUDENT_ID, response.getStudentId());
        assertTrue(response.isPresent());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        // Проверяем, что save был вызван с корректным объектом
        verify(attendanceRepository, times(1)).save(argThat(attendance ->
                attendance.getId() == null &&
                        attendance.getLessonId() == LESSON_ID &&
                        attendance.getStudentId() == STUDENT_ID &&
                        attendance.isPresent() == true &&
                        attendance.getCreatedAt() != null &&
                        attendance.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addAttendance_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException dbException = new RuntimeException("Database connection failed");
        when(attendanceRepository.save(any(Attendance.class)))
                .thenThrow(dbException);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.addAttendance(validRequest);
        });

        assertEquals(6001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database connection failed"));
        assertSame(dbException, exception.getCause());

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw ServerException with generic message when exception has null message")
    void addAttendance_ExceptionWithNullMessage_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException exception = new RuntimeException();
        when(attendanceRepository.save(any(Attendance.class))).thenThrow(exception);

        // When & Then
        ServerException serverException = assertThrows(ServerException.class, () -> {
            attendanceService.addAttendance(validRequest);
        });

        assertEquals("java.lang.RuntimeException", serverException.getMessage());
        assertEquals(6001, serverException.getErrorCode());
        assertSame(exception, serverException.getCause());
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void addAttendance_NullRequest_ThrowsException() throws ServerException {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            attendanceService.addAttendance(null);
        });

        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding attendance")
    void addAttendance_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(attendanceRepository.save(any(Attendance.class)))
                .thenAnswer(invocation -> {
                    Attendance saved = invocation.getArgument(0);
                    return new Attendance(
                            TEST_ID,
                            saved.getLessonId(),
                            saved.getStudentId(),
                            saved.isPresent(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        AttendanceResponse response = attendanceService.addAttendance(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        // Проверяем, что createdAt и updatedAt установлены
        verify(attendanceRepository).save(argThat(att ->
                att.getCreatedAt() != null &&
                        att.getUpdatedAt() != null &&
                        att.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    // getAttendanceById

    @Test
    @DisplayName("Should get attendance by id successfully when exists")
    void getAttendanceById_Success() throws ServerException {
        // Given
        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.of(attendance));

        // When
        AttendanceResponse response = attendanceService.getAttendanceById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LESSON_ID, response.getLessonId());
        assertEquals(STUDENT_ID, response.getStudentId());
        assertTrue(response.isPresent());

        verify(attendanceRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when attendance not found")
    void getAttendanceById_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.getAttendanceById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("attendance: " + TEST_ID + " not found"));
        assertEquals(6002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(attendanceRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getAttendanceById_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException dbException = new RuntimeException("Database error");
        when(attendanceRepository.findById(TEST_ID)).thenThrow(dbException);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.getAttendanceById(TEST_ID);
        });

        assertEquals(6002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));
        assertSame(dbException, exception.getCause());

        verify(attendanceRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should handle invalid id gracefully")
    void getAttendanceById_InvalidId_ThrowsServerException() throws ServerException {
        // Given
        long invalidId = -1L;
        when(attendanceRepository.findById(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid id"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.getAttendanceById(invalidId);
        });

        assertEquals(6002, exception.getErrorCode());
        verify(attendanceRepository, times(1)).findById(invalidId);
    }


    //getAttendances

    @Test
    @DisplayName("Should get all attendances successfully")
    void getAttendances_Success() throws ServerException {
        // Given
        List<Attendance> attendances = Arrays.asList(
                new Attendance(1L, 101L, 201L, true, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC)),
                new Attendance(2L, 102L, 202L, false, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC)),
                new Attendance(3L, 103L, 203L, true, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))
        );

        when(attendanceRepository.findAll()).thenReturn(attendances);

        // When
        List<AttendanceResponse> responses = attendanceService.getAttendances();

        // Then
        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals(3L, responses.get(2).getId());
        assertEquals(101L, responses.get(0).getLessonId());
        assertEquals(102L, responses.get(1).getLessonId());
        assertEquals(103L, responses.get(2).getLessonId());

        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no attendances exist")
    void getAttendances_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(attendanceRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<AttendanceResponse> responses = attendanceService.getAttendances();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getAttendances_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException dbException = new RuntimeException("Connection timeout");
        when(attendanceRepository.findAll()).thenThrow(dbException);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.getAttendances();
        });

        assertEquals(6003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Connection timeout"));
        assertSame(dbException, exception.getCause());

        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle null from repository when getting all")
    void getAttendances_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(attendanceRepository.findAll()).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.getAttendances();
        });

        assertEquals(6003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(attendanceRepository, times(1)).findAll();
    }

    //editAttendance

    @Test
    @DisplayName("Should edit attendance successfully when valid")
    void editAttendance_Success() throws ServerException {
        // Given
        AttendanceRequest editRequest = new AttendanceRequest();

        editRequest.setLessonId(999L);
        editRequest.setStudentId(888L);
        editRequest.setPresent(false);

        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.update(eq(TEST_ID), any(Attendance.class))).thenReturn(true);

        // When
        AttendanceResponse response = attendanceService.editAttendance(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(999L, response.getLessonId());
        assertEquals(888L, response.getStudentId());
        assertFalse(response.isPresent());

        // Проверяем, что updatedAt обновился
        assertNotNull(response.getUpdatedAt());

        // Проверяем, что update был вызван с обновленными данными
        verify(attendanceRepository, times(1)).findById(TEST_ID);
        verify(attendanceRepository, times(1)).update(eq(TEST_ID), argThat(att ->
                att.getLessonId() == 999L &&
                        att.getStudentId() == 888L &&
                        !att.isPresent() &&
                        att.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when editing non-existent attendance")
    void editAttendance_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.editAttendance(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("attendance: " + TEST_ID + " not found"));
        assertEquals(6004, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(attendanceRepository, times(1)).findById(TEST_ID);
        verify(attendanceRepository, never()).update(anyLong(), any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw ServerException when update fails")
    void editAttendance_UpdateFails_ThrowsServerException() throws ServerException {
        // Given
        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.update(eq(TEST_ID), any(Attendance.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.editAttendance(TEST_ID, validRequest);
        });

        assertEquals(6004, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Update failed"));

        verify(attendanceRepository, times(1)).findById(TEST_ID);
        verify(attendanceRepository, times(1)).update(eq(TEST_ID), any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception during find")
    void editAttendance_FindThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(attendanceRepository.findById(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            attendanceService.editAttendance(TEST_ID, validRequest);
        });

        assertEquals(6004, exception.getErrorCode());
        verify(attendanceRepository, times(1)).findById(TEST_ID);
        verify(attendanceRepository, never()).update(anyLong(), any(Attendance.class));
    }

    @Test
    @DisplayName("Should update timestamps when editing attendance")
    void editAttendance_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        attendance.setCreatedAt(oldTimestamp);
        attendance.setUpdatedAt(oldTimestamp);

        when(attendanceRepository.findById(TEST_ID)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.update(eq(TEST_ID), any(Attendance.class))).thenReturn(true);

        // When
        attendanceService.editAttendance(TEST_ID, validRequest);

        // Then
        verify(attendanceRepository).update(eq(TEST_ID), argThat(att ->
                att.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !att.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        att.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    //deleteAttendance

    @Test
    @DisplayName("Should delete attendance successfully when exists")
    void deleteAttendance_Success() throws ServerException {
        // Given
        when(attendanceRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        attendanceService.deleteAttendance(TEST_ID);

        // Then
        verify(attendanceRepository, times(1)).deleteById(TEST_ID);
    }
}