package com.example.edu_base.service.studentGroup;

import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
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
class StudentGroupServiceTest {

    @Mock
    IStudentGroupRepository studentGroupRepository;
    @Mock
    IStudentRepository studentRepository;

    @InjectMocks
    StudentGroupService studentGroupService;

    @Test
    void addStudentGroup_returnsResponse() {
        StudentGroupRequest req = new StudentGroupRequest();
        req.setGroupName("G-101");

        StudentGroup saved = new StudentGroup(7L, "G-101", ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));
        when(studentGroupRepository.save(any())).thenReturn(saved);

        StudentGroupResponse resp = studentGroupService.addStudentGroup(req);

        assertEquals(7L, resp.getId());
        assertEquals("G-101", resp.getGroupName());
    }

    @Test
    void deleteStudentGroup_throwsWhenNotEmpty() {
        when(studentRepository.findByStudentGroupId(8L)).thenReturn(List.of(new Student()));

        assertThrows(IllegalArgumentException.class, () -> studentGroupService.deleteStudentGroup(8L));
    }
}
