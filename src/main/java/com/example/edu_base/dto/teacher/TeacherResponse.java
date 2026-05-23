package com.example.edu_base.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {

    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;

}
