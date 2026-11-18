package com.expensetracker.expensetracker.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "You must enter an username")
    private String username;

    @Email(message = "You must enter a vaild email")
    @NotBlank(message = "You must enter an email")
    private String email;

    @NotBlank(message = "You must enter a password")
    private String password;

    @NotBlank(message = "You must select a currency.")
    private String currency;
}
