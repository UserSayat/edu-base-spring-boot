package com.example.edu_base.dto.student;

import com.example.edu_base.common.Status;
import com.example.edu_base.dto.StudentGroup.StudentGroupResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
