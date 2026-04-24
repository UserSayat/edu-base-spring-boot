package com.example.edu_base.dto.StudentGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // для @Builder
public class StudentGroupResponse {
    private Long id;

    private String groupName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
