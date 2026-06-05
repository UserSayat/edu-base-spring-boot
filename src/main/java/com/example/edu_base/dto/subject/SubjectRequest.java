package com.example.edu_base.dto.subject;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectRequest {

    @NotNull
    @Size(max = 20)
    private String subjectName;
}
