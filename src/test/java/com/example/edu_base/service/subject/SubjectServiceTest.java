package com.example.edu_base.service.subject;

import com.example.edu_base.dto.subject.SubjectRequest;
import com.example.edu_base.dto.subject.SubjectResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Subject;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.exception.ServerException;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
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
@DisplayName("Subject Service Unit Tests")
class SubjectServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ILessonRepository lessonRepository;

    @InjectMocks
    private SubjectService subjectService;

    private SubjectRequest validRequest;
    private Subject subject;
    private SubjectResponse expectedResponse;

    private final long TEST_ID = 1L;
    private final String SUBJECT_NAME = "Mathematics";

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new SubjectRequest();

        validRequest.setSubjectName(SUBJECT_NAME);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        subject = new Subject(
                TEST_ID,
                SUBJECT_NAME,
                now,
                now
        );

        expectedResponse = new SubjectResponse(
                TEST_ID,
                SUBJECT_NAME,
                now,
                now
        );
    }


    //addSubject

    @Test
    @DisplayName("Should add subject successfully when valid")
    void addSubject_Success() throws ServerException {
        // Given
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        // When
        SubjectResponse response = subjectService.addSubject(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(SUBJECT_NAME, response.getSubjectName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addSubject_RepositorySaveFails_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.save(any(Subject.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.addSubject(validRequest);
        });

        assertEquals(4001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw ServerException with generic message when exception has null message")
    void addSubject_ExceptionWithNullMessage_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException exception = new RuntimeException();
        when(subjectRepository.save(any(Subject.class))).thenThrow(exception);

        // When & Then
        ServerException serverException = assertThrows(ServerException.class, () -> {
            subjectService.addSubject(validRequest);
        });

        assertEquals("java.lang.RuntimeException", serverException.getMessage());
        assertEquals(4001, serverException.getErrorCode());
        assertSame(exception, serverException.getCause());

        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding subject")
    void addSubject_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(subjectRepository.save(any(Subject.class)))
                .thenAnswer(invocation -> {
                    Subject saved = invocation.getArgument(0);
                    return new Subject(
                            TEST_ID,
                            saved.getSubjectName(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        SubjectResponse response = subjectService.addSubject(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        verify(subjectRepository).save(argThat(subject ->
                subject.getCreatedAt() != null &&
                        subject.getUpdatedAt() != null &&
                        subject.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void addSubject_NullRequest_ThrowsException() throws ServerException {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            subjectService.addSubject(null);
        });

        verify(subjectRepository, never()).save(any());
    }


    //getSubjectById

    @Test
    @DisplayName("Should get subject by id successfully when exists")
    void getSubjectById_Success() throws ServerException {
        // Given
        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.of(subject));

        // When
        SubjectResponse response = subjectService.getSubjectById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(SUBJECT_NAME, response.getSubjectName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(subjectRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when subject not found")
    void getSubjectById_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.getSubjectById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("subject: " + TEST_ID + " not found"));
        assertEquals(4002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(subjectRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getSubjectById_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.getSubjectById(TEST_ID);
        });

        assertEquals(4002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(subjectRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should handle invalid id gracefully")
    void getSubjectById_InvalidId_ThrowsServerException() throws ServerException {
        // Given
        long invalidId = -1L;
        when(subjectRepository.findById(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid id"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.getSubjectById(invalidId);
        });

        assertEquals(4002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid id"));

        verify(subjectRepository, times(1)).findById(invalidId);
    }


    //getSubjects

    @Test
    @DisplayName("Should get all subjects successfully")
    void getSubjects_Success() throws ServerException {
        // Given
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Subject> subjects = Arrays.asList(
                new Subject(1L, "Mathematics", now, now),
                new Subject(2L, "Physics", now, now),
                new Subject(3L, "Chemistry", now, now)
        );

        when(subjectRepository.findAll()).thenReturn(subjects);

        // When
        List<SubjectResponse> responses = subjectService.getSubjects();

        // Then
        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Mathematics", responses.get(0).getSubjectName());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("Physics", responses.get(1).getSubjectName());
        assertEquals(3L, responses.get(2).getId());
        assertEquals("Chemistry", responses.get(2).getSubjectName());

        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no subjects exist")
    void getSubjects_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(subjectRepository.findAll()).thenReturn(List.of());

        // When
        List<SubjectResponse> responses = subjectService.getSubjects();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getSubjects_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.getSubjects();
        });

        assertEquals(4003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database connection failed"));

        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle null from repository")
    void getSubjects_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findAll()).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.getSubjects();
        });

        assertEquals(4003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(subjectRepository, times(1)).findAll();
    }


    //editSubject

    @Test
    @DisplayName("Should edit subject successfully when valid")
    void editSubject_Success() throws ServerException {
        // Given
        SubjectRequest editRequest = new SubjectRequest();

        editRequest.setSubjectName("Advanced Mathematics");

        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.of(subject));
        when(subjectRepository.update(any(Subject.class))).thenReturn(true);

        // When
        SubjectResponse response = subjectService.editSubject(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals("Advanced Mathematics", response.getSubjectName());

        verify(subjectRepository, times(1)).findById(TEST_ID);
        verify(subjectRepository, times(1)).update(argThat(subject ->
                subject.getSubjectName().equals("Advanced Mathematics") &&
                        subject.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when subject not found")
    void editSubject_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.editSubject(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("subject: " + TEST_ID + " not found"));
        assertEquals(4004, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(subjectRepository, times(1)).findById(TEST_ID);
        verify(subjectRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should update timestamp when editing subject")
    void editSubject_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        subject.setCreatedAt(oldTimestamp);
        subject.setUpdatedAt(oldTimestamp);

        SubjectRequest editRequest = new SubjectRequest();

        editRequest.setSubjectName("Updated Subject Name");

        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.of(subject));
        when(subjectRepository.update(any(Subject.class))).thenReturn(true);

        // When
        subjectService.editSubject(TEST_ID, editRequest);

        // Then
        verify(subjectRepository).update(argThat(subject ->
                subject.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !subject.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        subject.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when repository update fails")
    void editSubject_UpdateFails_ThrowsServerException() throws ServerException {
        // Given
        SubjectRequest editRequest = new SubjectRequest();

        editRequest.setSubjectName("New Subject Name");

        when(subjectRepository.findById(TEST_ID)).thenReturn(Optional.of(subject));
        when(subjectRepository.update(any(Subject.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            subjectService.editSubject(TEST_ID, editRequest);
        });

        assertEquals(4004, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Update failed"));

        verify(subjectRepository, times(1)).findById(TEST_ID);
        verify(subjectRepository, times(1)).update(any(Subject.class));
    }

    //deleteSubject

    @Test
    @DisplayName("Should delete subject successfully when no lessons exist")
    void deleteSubject_Success() throws ServerException {
        // Given
        when(lessonRepository.findBySubjectId(TEST_ID)).thenReturn(List.of());
        when(subjectRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        subjectService.deleteSubject(TEST_ID);

        // Then
        verify(lessonRepository, times(1)).findBySubjectId(TEST_ID);
        verify(subjectRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when subject has lessons")
    void deleteSubject_SubjectHasLessons_ThrowsIllegalArgumentException() throws ServerException {
        // Given
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Lesson> lessons = Arrays.asList(
                new Lesson(1L, TEST_ID, LocalDate.now(), 1L, 100L, 200L, now, now),
                new Lesson(2L, TEST_ID, LocalDate.now(), 2L, 100L, 200L, now, now)
        );

        when(lessonRepository.findBySubjectId(TEST_ID)).thenReturn(lessons);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subjectService.deleteSubject(TEST_ID);
        });

        assertEquals("can not delete subject while lessons with it exist", exception.getMessage());

        verify(lessonRepository, times(1)).findBySubjectId(TEST_ID);
        verify(subjectRepository, never()).deleteById(anyLong());
    }
}