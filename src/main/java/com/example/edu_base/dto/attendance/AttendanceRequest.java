package com.example.edu_base.dto.attendance;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceRequest {

    private Long id;

    private Long lessonId;
    private Long studentId;
    private boolean present;
}
