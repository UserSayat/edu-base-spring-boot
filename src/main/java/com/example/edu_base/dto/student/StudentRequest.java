package com.example.edu_base.dto.student;

import com.example.edu_base.common.StudentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentRequest {

    @NotNull
    @Size(max = 20)
    private String lastName;

    @NotNull
    @Size(max = 20)
    private String firstName;

    @NotNull
    @Size(max = 20)
    private String middleName;

    private StudentStatus studentStatus;

    @NotNull
    private Long studentGroupId;

}
