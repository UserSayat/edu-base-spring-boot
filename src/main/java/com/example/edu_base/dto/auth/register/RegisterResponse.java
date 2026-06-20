package com.example.edu_base.dto.auth.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String username;
    private Set<String> role;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
