package com.example.edu_base.service.student;

import com.example.edu_base.common.StudentStatus;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.exception.ServerException;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.dto.student.StudentRequest;
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
@DisplayName("Student Service Unit Tests")
class StudentServiceTest {

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private IStudentGroupRepository studentGroupRepository;

    @Mock
    private IAttendanceRepository attendanceRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentRequest validRequest;
    private Student student;
    private StudentResponse expectedResponse;
    private StudentGroup studentGroup;
    private StudentGroupResponse studentGroupResponse;

    private final long TEST_ID = 1L;
    private final long GROUP_ID = 100L;
    private final String LAST_NAME = "Ivanov";
    private final String FIRST_NAME = "Ivan";
    private final String MIDDLE_NAME = "Ivanovich";
    private final StudentStatus STATUS = StudentStatus.STUDYING;
    private final String GROUP_NAME = "Group A";

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        validRequest = new StudentRequest();

        validRequest.setLastName(LAST_NAME);
        validRequest.setFirstName(FIRST_NAME);
        validRequest.setMiddleName(MIDDLE_NAME);
        validRequest.setStudentStatus(STATUS);
        validRequest.setStudentGroupId(GROUP_ID);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        student = new Student(
                TEST_ID,
                LAST_NAME,
                FIRST_NAME,
                MIDDLE_NAME,
                STATUS,
                GROUP_ID,
                now,
                now
        );

        studentGroup = new StudentGroup(
                GROUP_ID,
                GROUP_NAME,
                now,
                now
        );

        studentGroupResponse = new StudentGroupResponse(
                GROUP_ID,
                GROUP_NAME,
                now,
                now
        );

        expectedResponse = new StudentResponse(
                TEST_ID,
                LAST_NAME,
                FIRST_NAME,
                MIDDLE_NAME,
                STATUS,
                studentGroupResponse,
                now,
                now
        );
    }


    //addStudent

    @Test
    @DisplayName("Should add student successfully when valid")
    void addStudent_Success() throws ServerException {
        // Given
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse response = studentService.addStudent(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LAST_NAME, response.getLastName());
        assertEquals(FIRST_NAME, response.getFirstName());
        assertEquals(MIDDLE_NAME, response.getMiddleName());
        assertEquals(STATUS, response.getStudentStatus());
        assertEquals(GROUP_ID, response.getStudentGroup().getId());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(studentGroupRepository, times(2)).findById(GROUP_ID);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found")
    void addStudent_StudentGroupNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.addStudent(validRequest);
        });

        assertTrue(exception.getMessage().contains("group with id:" + GROUP_ID + " not found"));
        assertEquals(2001, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(studentGroupRepository, times(1)).findById(GROUP_ID);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ServerException when repository save fails")
    void addStudent_RepositorySaveFails_ThrowsServerException() throws ServerException {
        // Given
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(studentRepository.save(any(Student.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.addStudent(validRequest);
        });

        assertEquals(2001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(studentGroupRepository, times(1)).findById(GROUP_ID);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should set correct UTC timestamps when adding student")
    void addStudent_SetsCorrectTimestamps() throws ServerException {
        // Given
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> {
                    Student saved = invocation.getArgument(0);
                    return new Student(
                            TEST_ID,
                            saved.getLastName(),
                            saved.getFirstName(),
                            saved.getMiddleName(),
                            saved.getStatus(),
                            saved.getStudentGroupId(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                });

        // When
        StudentResponse response = studentService.addStudent(validRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());

        verify(studentRepository).save(argThat(student ->
                student.getCreatedAt() != null &&
                        student.getUpdatedAt() != null &&
                        student.getCreatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void addStudent_NullRequest_ThrowsException() throws ServerException {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            studentService.addStudent(null);
        });

        verify(studentGroupRepository, never()).findById(anyLong());
        verify(studentRepository, never()).save(any());
    }


    //getStudentById

    @Test
    @DisplayName("Should get student by id successfully when exists")
    void getStudentById_Success() throws ServerException {
        // Given
        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));

        // When
        StudentResponse response = studentService.getStudentById(TEST_ID);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals(LAST_NAME, response.getLastName());
        assertEquals(FIRST_NAME, response.getFirstName());
        assertEquals(MIDDLE_NAME, response.getMiddleName());
        assertEquals(STATUS, response.getStudentStatus());
        assertEquals(GROUP_ID, response.getStudentGroup().getId());
        assertEquals(GROUP_NAME, response.getStudentGroup().getGroupName());

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(GROUP_ID);
    }

    @Test
    @DisplayName("Should throw ServerException when student not found")
    void getStudentById_StudentNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.getStudentById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("student: " + TEST_ID + " not found"));
        assertEquals(2002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getStudentById_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findById(TEST_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.getStudentById(TEST_ID);
        });

        assertEquals(2002, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when student group not found in toStudentResponse")
    void getStudentById_StudentGroupNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.getStudentById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("group with id: " + GROUP_ID + " not found"));
        assertEquals(2002, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof EntityNotFoundException);

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(GROUP_ID);
    }


    //getStudentsByGroup

    @Test
    @DisplayName("Should get students by group successfully")
    void getStudentsByGroup_Success() throws ServerException {
        // Given
        List<Student> students = Arrays.asList(
                new Student(1L, "Petrov", "Petr", "Petrovich", STATUS, GROUP_ID,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC)),
                new Student(2L, "Sidorov", "Sidor", "Sidorovich", STATUS, GROUP_ID,
                        ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))
        );

        when(studentRepository.findByStudentGroupId(GROUP_ID)).thenReturn(students);
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));

        // When
        List<StudentResponse> responses = studentService.getStudentsByGroup(GROUP_ID);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Petrov", responses.get(0).getLastName());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("Sidorov", responses.get(1).getLastName());

        // Проверяем, что группа правильно установлена для каждого студента
        for (StudentResponse response : responses) {
            assertEquals(GROUP_ID, response.getStudentGroup().getId());
            assertEquals(GROUP_NAME, response.getStudentGroup().getGroupName());
        }

        verify(studentRepository, times(1)).findByStudentGroupId(GROUP_ID);
        verify(studentGroupRepository, times(2)).findById(GROUP_ID);
    }

    @Test
    @DisplayName("Should return empty list when no students in group")
    void getStudentsByGroup_EmptyList_ReturnsEmptyList() throws ServerException {
        // Given
        when(studentRepository.findByStudentGroupId(GROUP_ID)).thenReturn(List.of());

        // When
        List<StudentResponse> responses = studentService.getStudentsByGroup(GROUP_ID);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(studentRepository, times(1)).findByStudentGroupId(GROUP_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw ServerException when repository throws exception")
    void getStudentsByGroup_RepositoryThrowsException_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findByStudentGroupId(GROUP_ID))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.getStudentsByGroup(GROUP_ID);
        });

        assertEquals(2003, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Database error"));

        verify(studentRepository, times(1)).findByStudentGroupId(GROUP_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should handle null from repository")
    void getStudentsByGroup_RepositoryReturnsNull_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findByStudentGroupId(GROUP_ID)).thenReturn(null);

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.getStudentsByGroup(GROUP_ID);
        });

        assertEquals(2003, exception.getErrorCode());
        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(studentRepository, times(1)).findByStudentGroupId(GROUP_ID);
    }


    //editStudent

    @Test
    @DisplayName("Should edit student successfully when valid")
    void editStudent_Success() throws ServerException {
        // Given
        StudentRequest editRequest = new StudentRequest();

        editRequest.setLastName("Petrova");
        editRequest.setFirstName("Anna");
        editRequest.setMiddleName("Sergeevna");
        editRequest.setStudentStatus(StudentStatus.EXPELLED);
        editRequest.setStudentGroupId(200L);

        StudentGroup newGroup = new StudentGroup(200L, "Group B",
                ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));

        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(200L)).thenReturn(Optional.of(newGroup));
        when(studentRepository.update(any(Student.class))).thenReturn(true);

        // When
        StudentResponse response = studentService.editStudent(TEST_ID, editRequest);

        // Then
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        assertEquals("Petrova", response.getLastName());
        assertEquals("Anna", response.getFirstName());
        assertEquals("Sergeevna", response.getMiddleName());
        assertEquals(StudentStatus.EXPELLED, response.getStudentStatus());
        assertEquals(200L, response.getStudentGroup().getId());

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(2)).findById(200L);
        verify(studentRepository, times(1)).update(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ServerException when student not found")
    void editStudent_StudentNotFound_ThrowsServerException() throws ServerException {
        // Given
        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.editStudent(TEST_ID, validRequest);
        });

        assertTrue(exception.getMessage().contains("student: " + TEST_ID + " not found"));
        assertEquals(2004, exception.getErrorCode());

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, never()).findById(anyLong());
        verify(studentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should throw ServerException when new student group not found")
    void editStudent_NewGroupNotFound_ThrowsServerException() throws ServerException {
        // Given
        StudentRequest editRequest = new StudentRequest();

        editRequest.setLastName(LAST_NAME);
        editRequest.setFirstName(FIRST_NAME);
        editRequest.setMiddleName(MIDDLE_NAME);
        editRequest.setStudentStatus(STATUS);
        editRequest.setStudentGroupId(999L);

        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ServerException exception = assertThrows(ServerException.class, () -> {
            studentService.editStudent(TEST_ID, editRequest);
        });

        assertTrue(exception.getMessage().contains("group with id: 999 not found"));
        assertEquals(2004, exception.getErrorCode());

        verify(studentRepository, times(1)).findById(TEST_ID);
        verify(studentGroupRepository, times(1)).findById(999L);
        verify(studentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should update timestamp when editing student")
    void editStudent_UpdatesTimestamp() throws ServerException {
        // Given
        ZonedDateTime oldTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1);
        student.setCreatedAt(oldTimestamp);
        student.setUpdatedAt(oldTimestamp);

        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(studentGroup));
        when(studentRepository.update(any(Student.class))).thenReturn(true);

        // When
        studentService.editStudent(TEST_ID, validRequest);

        // Then
        verify(studentRepository).update(argThat(student ->
                student.getCreatedAt().equals(oldTimestamp) && // createdAt не меняется
                        !student.getUpdatedAt().equals(oldTimestamp) && // updatedAt меняется
                        student.getUpdatedAt().getOffset().equals(ZoneOffset.UTC)
        ));
    }

    @Test
    @DisplayName("Should update all fields when editing")
    void editStudent_UpdatesAllFields() throws ServerException {
        // Given
        StudentRequest editRequest = new StudentRequest();

        editRequest.setLastName("NewLastName");
        editRequest.setFirstName("NewFirstName");
        editRequest.setMiddleName("NewMiddleName");
        editRequest.setStudentStatus(StudentStatus.EXPELLED);
        editRequest.setStudentGroupId(200L);

        StudentGroup newGroup = new StudentGroup(200L, "New Group",
                ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));

        when(studentRepository.findById(TEST_ID)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(200L)).thenReturn(Optional.of(newGroup));
        when(studentRepository.update(any(Student.class))).thenReturn(true);

        // When
        StudentResponse response = studentService.editStudent(TEST_ID, editRequest);

        // Then
        verify(studentRepository).update(argThat(student ->
                student.getLastName().equals("NewLastName") &&
                        student.getFirstName().equals("NewFirstName") &&
                        student.getMiddleName().equals("NewMiddleName") &&
                        student.getStatus() == StudentStatus.EXPELLED &&
                        student.getStudentGroupId() == 200L
        ));
    }


    //deleteStudent

    @Test
    @DisplayName("Should delete student and related attendance successfully")
    void deleteStudent_Success() throws ServerException {
        // Given
        when(studentRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        studentService.deleteStudent(TEST_ID);

        // Then
        verify(studentRepository, times(1)).deleteById(TEST_ID);
        verify(attendanceRepository, times(1)).deleteByStudentId(TEST_ID);
    }

    @Test
    @DisplayName("Should delete attendance even if attendance deletion returns 0")
    void deleteStudent_AttendanceDeletionReturnsZero_Success() throws ServerException {
        // Given
        when(studentRepository.deleteById(TEST_ID)).thenReturn(true);

        // When
        studentService.deleteStudent(TEST_ID);

        // Then
        verify(studentRepository, times(1)).deleteById(TEST_ID);
        verify(attendanceRepository, times(1)).deleteByStudentId(TEST_ID);
    }
}