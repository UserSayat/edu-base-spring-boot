package com.example.edu_base.dto.auth.register;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    @Size(max = 20)
    private String username;
    @NotNull
    @Size(min = 6, max = 20)
    private String password;
    private String role;

}
