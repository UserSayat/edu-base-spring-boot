package com.example.edu_base.dto;

import com.example.edu_base.common.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor // для десериализации
@AllArgsConstructor // для @Builder
public class StudentDto {
    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private Status status;
    private StudentGroupDto studentGroup;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
