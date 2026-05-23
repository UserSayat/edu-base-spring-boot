package com.example.edu_base.dto.teacher;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeacherRequest {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String lastName;
    @NotNull
    @Size(max = 20)
    private String firstName;

    private String middleName;

}
