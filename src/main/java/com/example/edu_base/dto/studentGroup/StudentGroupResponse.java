package com.example.edu_base.dto.studentGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGroupResponse {
    private Long id;

    private String groupName;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
