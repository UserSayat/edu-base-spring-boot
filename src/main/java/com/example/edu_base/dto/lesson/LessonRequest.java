package com.example.edu_base.dto.lesson;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LessonRequest {

    private Long id;

    private Long subjectId;
    private LocalDate date;
    private Long pairNumber;
    private Long teacherId;
    private Long studentGroupId;
}
