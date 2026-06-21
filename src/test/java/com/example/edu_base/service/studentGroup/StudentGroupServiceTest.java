package com.example.edu_base.service.studentGroup;

import com.example.edu_base.common.StudentStatus;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.exception.ServerException;
import com.example.edu_base.repository.student.IStudentRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
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
@DisplayName("Student Group Service Unit Tests")
class StudentGroupServiceTest {

    @Mock
    private IStudentGroupRepository studentGroupRepository;

    @Mock
    private IStudentRepository studentRepository;

    @InjectMocks
    private StudentGroupService studentGroupService;

    private StudentGroupRequest validRequest;
    private StudentGroup studentGroup;
    private StudentGroupResponse expectedResponse;

    private final long TEST_ID = 1L;
    private final String GROUP_NAME = "Group A";

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new StudentGroupRequest();

        validRequest.setGroupName(GROUP_NAME);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        studentGroup = new StudentGroup(
                TEST_ID,
                GROUP_NAME,
                now,
                now
        );

        expectedResponse = new StudentGroupResponse(
                TEST_ID,
                GROUP_NAME,
                now,
                now
        );
    }


    //addStudentGroup

    @Test
    @DisplayName("Should add student group successfully when valid")
    void addStudentGroup_Success() throws ServerException {
        // Given
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(studentGroup);

        // When
        StudentGroupResponse response = studentGroupService.addStudentGroup(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(GROUP_NAME, response.getGroupName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(studentGroupRepository, times(1)).save(any(StudentGroup.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addStudentGroup_RepositorySaveFails_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.save(any(StudentGroup.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.addStudentGroup(validRequest);
        });

        assertEquals(1001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(studentGroupRepository, times(1)).save(any(StudentGroup.class));
    }

    @Test
    @DisplayName("Should throw ServerException with generic message when exception has null message")
    void addStudentGroup_ExceptionWithNullMessage_ThrowsServerException() throws ServerException {
        // Given
        RuntimeException exception = new RuntimeException();
        when(studentGroupRepository.save(any(StudentGroup.class))).thenThrow(exception);

        // When & Then
        ServerException serverException = assertThrows(ServerException.class, () -> {
            studentGroupService.addStudentGroup(validRequest);
        });

        assertEquals("java.lang.RuntimeException", serverException.getMessage());
        assertEquals(1001, serverException.getErrorCode());
        assertSame(exception, serverException.getCause());

        verify(studentGroupRepository, times(1)).save(any(StudentGroup.class));
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding student group")
    void addStudentGroup_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(studentGroupRepository.save(any(StudentGroup.class)))
                .thenAnswer(invocation -> {
                    StudentGroup saved = invocation.getArgument(0);
                    return new StudentGroup(
                            TEST_ID,
                            saved.getGroupName(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        StudentGroupResponse response = studentGroupService.addStudentGroup(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        verify(studentGroupRepository).save(argThat(group ->
                group.getCreatedAt() != null &&
                        group.getUpdatedAt() != null &&
                        group.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void addStudentGroup_NullRequest_ThrowsException() throws ServerException {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            studentGroupService.addStudentGroup(null);
        });

        verify(studentGroupRepository, never()).save(any());
    }


    //getStudentGroupById

    @Test
    @DisplayName("Should get student group by id successfully when exists")
    void getStudentGroupById_Success() throws ServerException {
        // Given
        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.of(studentGroup));

        // When
        StudentGroupResponse response = studentGroupService.getStudentGroupById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(GROUP_NAME, response.getGroupName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found")
    void getStudentGroupById_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.getStudentGroupById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("student group: " + TEST_ID + " not found"));
        assertEquals(1002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getStudentGroupById_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findById(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.getStudentGroupById(TEST_ID);
        });

        assertEquals(1002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("Should handle invalid id gracefully")
    void getStudentGroupById_InvalidId_ThrowsServerException() throws ServerException {
        // Given
        long invalidId = -1L;
        when(studentGroupRepository.findById(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid id"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.getStudentGroupById(invalidId);
        });

        assertEquals(1002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid id"));

        verify(studentGroupRepository, times(1)).findById(invalidId);
    }


    //getStudentGroups

    @Test
    @DisplayName("Should get all student groups successfully")
    void getStudentGroups_Success() throws ServerException {
        // Given
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<StudentGroup> groups = Arrays.asList(
                new StudentGroup(1L, "Group A", now, now),
                new StudentGroup(2L, "Group B", now, now),
                new StudentGroup(3L, "Group C", now, now)
        );

        when(studentGroupRepository.findAll()).thenReturn(groups);

        // When
        List<StudentGroupResponse> responses = studentGroupService.getStudentGroups();

        // Then
        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Group A", responses.get(0).getGroupName());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("Group B", responses.get(1).getGroupName());
        assertEquals(3L, responses.get(2).getId());
        assertEquals("Group C", responses.get(2).getGroupName());

        verify(studentGroupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no student groups exist")
    void getStudentGroups_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(studentGroupRepository.findAll()).thenReturn(List.of());

        // When
        List<StudentGroupResponse> responses = studentGroupService.getStudentGroups();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(studentGroupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getStudentGroups_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.getStudentGroups();
        });

        assertEquals(1003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database connection failed"));

        verify(studentGroupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle null from repository")
    void getStudentGroups_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findAll()).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.getStudentGroups();
        });

        assertEquals(1003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(studentGroupRepository, times(1)).findAll();
    }


    //editStudentGroup

    @Test
    @DisplayName("Should edit student group successfully when valid")
    void editStudentGroup_Success() throws ServerException {
        // Given
        StudentGroupRequest editRequest = new StudentGroupRequest();

        editRequest.setGroupName("New Group Name");

        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.of(studentGroup));
        when(studentGroupRepository.update(any(StudentGroup.class))).thenReturn(true);

        // When
        StudentGroupResponse response = studentGroupService.editStudentGroup(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals("New Group Name", response.getGroupName());

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).update(argThat(group ->
                group.getGroupName().equals("New Group Name") &&
                        group.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found")
    void editStudentGroup_NotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.editStudentGroup(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("student group: " + TEST_ID + " not found"));
        assertEquals(1004, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should update timestamp when editing student group")
    void editStudentGroup_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        studentGroup.setCreatedAt(oldTimestamp);
        studentGroup.setUpdatedAt(oldTimestamp);

        StudentGroupRequest editRequest = new StudentGroupRequest();

        editRequest.setGroupName("Updated Group Name");

        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.of(studentGroup));
        when(studentGroupRepository.update(any(StudentGroup.class))).thenReturn(true);

        // When
        studentGroupService.editStudentGroup(TEST_ID, editRequest);

        // Then
        verify(studentGroupRepository).update(argThat(group ->
                group.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !group.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        group.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should throw ServerException when repository update fails")
    void editStudentGroup_UpdateFails_ThrowsServerException() throws ServerException {
        // Given
        StudentGroupRequest editRequest = new StudentGroupRequest();

        editRequest.setGroupName("New Group Name");

        when(studentGroupRepository.findById(TEST_ID)).thenReturn(Optional.of(studentGroup));
        when(studentGroupRepository.update(any(StudentGroup.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.editStudentGroup(TEST_ID, editRequest);
        });

        assertEquals(1004, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Update failed"));

        verify(studentGroupRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).update(any(StudentGroup.class));
    }


    //deleteStudentGroup

    @Test
    @DisplayName("Should delete student group successfully when empty")
    void deleteStudentGroup_Success() throws ServerException {
        // Given
        when(studentRepository.findByStudentGroupId(TEST_ID)).thenReturn(List.of());
        when(studentGroupRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        studentGroupService.deleteStudentGroup(TEST_ID);

        // Then
        verify(studentRepository, times(1)).findByStudentGroupId(TEST_ID);
        verify(studentGroupRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when group has students")
    void deleteStudentGroup_GroupHasStudents_ThrowsIllegalArgumentException() throws ServerException {
        // Given
        List<Student> students = Arrays.asList(
                new Student(1L, "Ivanov", "Ivan", "Ivanovich",
                        StudentStatus.STUDYING, TEST_ID,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC)),
                new Student(2L, "Petrov", "Petr", "Petrovich",
                        StudentStatus.STUDYING, TEST_ID,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))
        );

        when(studentRepository.findByStudentGroupId(TEST_ID)).thenReturn(students);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.deleteStudentGroup(TEST_ID);
        });

        assertEquals("student group wasn't delete", exception.getMessage());

        verify(studentRepository, times(1)).findByStudentGroupId(TEST_ID);
        verify(studentGroupRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when student repository throws exception")
    void deleteStudentGroup_StudentRepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findByStudentGroupId(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentGroupService.deleteStudentGroup(TEST_ID);
        });

        assertEquals(1005, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("student group wasn't delete"));

        verify(studentRepository, times(1)).findByStudentGroupId(TEST_ID);
        verify(studentGroupRepository, never()).deleteById(anyLong());
    }
}