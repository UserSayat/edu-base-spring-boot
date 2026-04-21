package com.example.edu_base.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor // для десериализации
@AllArgsConstructor // для @Builder
public class StudentGroupDto {
    private Long id;

    @NotNull
    @Size(max = 20)
    private String groupName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
