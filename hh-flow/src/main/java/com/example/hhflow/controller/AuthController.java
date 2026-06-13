package com.example.hhflow.controller;

import com.example.hhflow.dto.request.LoginRequest;
import com.example.hhflow.dto.request.RefreshTokenRequest;
import com.example.hhflow.dto.request.RegisterApplicantRequest;
import com.example.hhflow.dto.request.RegisterEmployerRequest;
import com.example.hhflow.dto.response.AuthResponse;
import com.example.hhflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/employer")
    public AuthResponse registerEmployer(@Valid @RequestBody RegisterEmployerRequest request) {
        return authService.registerEmployer(request);
    }

    @PostMapping("/register/applicant")
    public AuthResponse registerApplicant(@Valid @RequestBody RegisterApplicantRequest request) {
        return authService.registerApplicant(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }
}
