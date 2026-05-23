package com.example.edu_base.dto.studentGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentGroupRequest {

        private Long id;

        @NotNull
        @Size(max = 20)
        private String groupName;
}
