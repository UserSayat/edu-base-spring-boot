package com.example.edu_base.service.student;

import com.example.edu_base.common.StudentStatus;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
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
class StudentServiceTest {

    @Mock
    IStudentRepository studentRepository;
    @Mock
    IStudentGroupRepository studentGroupRepository;
    @Mock
    IAttendanceRepository attendanceRepository;

    @InjectMocks
    StudentService studentService;

    @Test
    void addStudent_returnsResponse() {
        StudentRequest req = new StudentRequest();
        req.setLastName("Ivanov");
        req.setFirstName("Ivan");
        req.setMiddleName("I.");
        req.setStudentGroupId(2L);
        req.setStudentStatus(StudentStatus.STUDYING);

        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(new StudentGroup(2L, "G1", ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))));
        Student saved = new Student(11L, "Ivanov", "Ivan", "I.", StudentStatus.STUDYING, 2L, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(studentRepository.save(any())).thenReturn(saved);

        StudentResponse resp = studentService.addStudent(req);

        assertEquals(11L, resp.getId());
        assertEquals("Ivanov", resp.getLastName());
        assertEquals(2L, resp.getStudentGroup().getId());
    }

    @Test
    void getStudentsByGroup_returnsList() {
        Student s = new Student(12L, "Pupkin", "Vasya", "V.", StudentStatus.STUDYING, 2L, ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(studentRepository.findByStudentGroupId(2L)).thenReturn(List.of(s));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(new StudentGroup(2L, "G1", ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC))));
        var list = studentService.getStudentsByGroup(2L);

        assertEquals(1, list.size());
        assertEquals(12L, list.get(0).getId());
    }

}
