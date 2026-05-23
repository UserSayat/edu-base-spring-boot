package com.example.edu_base.dto.lesson;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private Long id;

    private Long subjectId;
    private LocalDate date;
    private Long pairNumber;

    private Long teacherId;
    private Long studentGroupId;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
