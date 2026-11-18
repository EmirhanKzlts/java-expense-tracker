package com.expensetracker.expensetracker.service.impl;

import com.expensetracker.expensetracker.dtos.auth.AuthResponse;
import com.expensetracker.expensetracker.dtos.auth.LoginRequest;
import com.expensetracker.expensetracker.dtos.auth.RegisterRequest;
import com.expensetracker.expensetracker.exception.BadRequestException;
import com.expensetracker.expensetracker.exception.UnauthorizedException;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.UserRepository;
import com.expensetracker.expensetracker.security.CustomUserDetails;
import com.expensetracker.expensetracker.security.JwtService;
import com.expensetracker.expensetracker.service.AuthService;
import com.expensetracker.expensetracker.service.DefaultCategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DefaultCategoryService defaultCategoryService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCurrency(registerRequest.getCurrency());

        User savedUser = userRepository.save(user);

        defaultCategoryService.createDefaultCategories(savedUser);

        String token = jwtService.generateToken(new CustomUserDetails(savedUser));

        return new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), savedUser.getCurrency());
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getCurrency());
    }
}
