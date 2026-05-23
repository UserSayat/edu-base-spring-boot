package com.example.edu_base.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {

    private Long id;

    private String subjectName;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
