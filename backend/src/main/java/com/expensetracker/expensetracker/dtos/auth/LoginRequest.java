package com.expensetracker.expensetracker.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequest {

    @NotBlank(message = "Email can not be blank")
    private String email;

    @NotBlank(message = "Password can not be blank")
    private String password;
}
