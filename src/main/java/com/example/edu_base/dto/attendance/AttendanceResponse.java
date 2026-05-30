package com.example.edu_base.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;

    private Long lessonId;
    private Long studentId;
    private boolean isPresent;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
