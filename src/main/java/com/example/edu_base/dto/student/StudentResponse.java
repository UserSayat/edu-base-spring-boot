package com.example.edu_base.dto.student;

import com.example.edu_base.common.Status;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // для @Builder
public class StudentResponse {
    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private Status status;
    private StudentGroupResponse studentGroup;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
