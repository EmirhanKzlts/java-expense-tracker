package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dtos.auth.AuthResponse;
import com.expensetracker.expensetracker.dtos.auth.LoginRequest;
import com.expensetracker.expensetracker.dtos.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
