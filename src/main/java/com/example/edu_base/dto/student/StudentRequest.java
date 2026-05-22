package com.example.edu_base.dto.student;

import com.example.edu_base.common.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // для @Builder
public class StudentRequest {
    private Long id;

    @NotNull
    @Size(max = 20)
    private String lastName;

    @NotNull
    @Size(max = 20)
    private String firstName;

    @NotNull
    @Size(max = 20)
    private String middleName;

    private Status status;

    @NotNull
    private Long studentGroupId;
}
