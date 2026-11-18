package com.expensetracker.expensetracker.dtos.auth;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String currency;

    public AuthResponse(String token, String username, String email, String currency) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.currency = currency;
    }

}
