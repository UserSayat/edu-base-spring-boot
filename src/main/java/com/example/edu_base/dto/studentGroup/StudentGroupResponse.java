package com.example.edu_base.dto.studentGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // для @Builder
public class StudentGroupResponse {
    private Long id;

    private String groupName;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
