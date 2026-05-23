package com.example.edu_base.dto.student;

import com.example.edu_base.common.StudentStatus;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private StudentStatus studentStatus;
    private StudentGroupResponse studentGroup;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
