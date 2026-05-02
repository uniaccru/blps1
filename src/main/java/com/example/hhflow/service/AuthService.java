package com.example.hhflow.service;

import com.example.hhflow.dto.request.LoginRequest;
import com.example.hhflow.dto.request.RefreshTokenRequest;
import com.example.hhflow.dto.request.RegisterApplicantRequest;
import com.example.hhflow.dto.request.RegisterEmployerRequest;
import com.example.hhflow.dto.response.AuthResponse;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.model.Applicant;
import com.example.hhflow.model.Employer;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.UserAccount;
import com.example.hhflow.repository.EmployerRepository;
import com.example.hhflow.repository.ApplicantRepository;
import com.example.hhflow.repository.UserAccountRepository;
import com.example.hhflow.security.CustomUserDetails;
import com.example.hhflow.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final EmployerRepository employerRepository;
    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse registerEmployer(RegisterEmployerRequest request) {
        if (userAccountRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone already registered: " + request.getPhone());
        }
        if (employerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Employer already exists with email: " + request.getEmail());
        }

        Employer employer = new Employer();
        employer.setEmail(request.getEmail());
        employer = employerRepository.save(employer);

        UserAccount account = new UserAccount();
        account.setPhone(request.getPhone().trim());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.EMPLOYER);
        account.setEmployer(employer);
        account.setApplicant(null);
        userAccountRepository.save(account);

        UserAccount persisted = userAccountRepository.findByPhone(account.getPhone())
                .orElseThrow(() -> new IllegalStateException("User account not persisted"));
        return tokensFor(persisted);
    }

    @Transactional
    public AuthResponse registerApplicant(RegisterApplicantRequest request) {
        if (userAccountRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone already registered: " + request.getPhone());
        }

        Applicant applicant = applicantRepository.save(new Applicant());

        UserAccount account = new UserAccount();
        account.setPhone(request.getPhone().trim());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.APPLICANT);
        account.setEmployer(null);
        account.setApplicant(applicant);
        userAccountRepository.save(account);

        UserAccount persisted = userAccountRepository.findByPhone(account.getPhone())
                .orElseThrow(() -> new IllegalStateException("User account not persisted"));
        return tokensFor(persisted);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword()));
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return tokensFor(details.getUserAccount());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String raw = request.getRefreshToken().trim();
        if (!jwtTokenProvider.isValidRefreshToken(raw)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        long accountId = jwtTokenProvider.parseRefreshAccountId(raw);
        UserAccount stub = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        UserAccount full = userAccountRepository.findByPhone(stub.getPhone())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        return tokensFor(full);
    }

    private AuthResponse tokensFor(UserAccount account) {
        return new AuthResponse(
                jwtTokenProvider.createAccessToken(account),
                jwtTokenProvider.createRefreshToken(account),
                "Bearer"
        );
    }
}
