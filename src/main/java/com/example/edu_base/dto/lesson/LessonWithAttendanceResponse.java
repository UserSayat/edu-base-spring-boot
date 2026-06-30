package com.example.edu_base.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonWithAttendanceResponse {

    private Long id;

    private Long subjectId;
    private LocalDate date;
    private Long pairNumber;

    private Long teacherId;
    private Long studentGroupId;

    private List<Pair<String, String>> attendance;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
