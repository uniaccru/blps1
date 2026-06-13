package com.example.hhflow.service;

import com.example.hhflow.dto.request.LoginRequest;
import com.example.hhflow.dto.request.RefreshTokenRequest;
import com.example.hhflow.dto.request.RegisterApplicantRequest;
import com.example.hhflow.dto.request.RegisterEmployerRequest;
import com.example.hhflow.dto.response.AuthResponse;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import com.example.hhflow.repository.UserRepository;
import com.example.hhflow.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse registerEmployer(RegisterEmployerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.EMPLOYER);
        user.setPhone(null);
        userRepository.save(user);

        User persisted = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not persisted"));
        return tokensFor(persisted);
    }

    @Transactional
    public AuthResponse registerApplicant(RegisterApplicantRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.APPLICANT);
        user.setPhone(null);
        userRepository.save(user);

        User persisted = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not persisted"));
        return tokensFor(persisted);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        return tokensFor(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String raw = request.getRefreshToken().trim();
        if (!jwtTokenProvider.isValidRefreshToken(raw)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        long userId = jwtTokenProvider.parseRefreshAccountId(raw);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        return tokensFor(user);
    }

    private AuthResponse tokensFor(User account) {
        return new AuthResponse(
                jwtTokenProvider.createAccessToken(account),
                jwtTokenProvider.createRefreshToken(account),
                "Bearer"
        );
    }
}
