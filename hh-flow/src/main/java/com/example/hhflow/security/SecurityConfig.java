package com.example.hhflow.security;

import com.example.hhflow.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.hhflow.security.jwt.JwtAuthenticationFilter;
import com.example.hhflow.security.jwt.JwtProperties;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(this::writeUnauthorized)
            .accessDeniedHandler(this::writeForbidden));
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/").permitAll()
            .requestMatchers("/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/vacancies", "/api/v1/vacancies/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/applications/submit").hasRole("APPLICANT")
            .requestMatchers("/api/v1/resumes/**").hasRole("APPLICANT")
            .requestMatchers(HttpMethod.GET, "/api/v1/applications", "/api/v1/applications/*").hasRole("EMPLOYER")
            .requestMatchers(HttpMethod.POST, "/api/v1/vacancies").hasRole("EMPLOYER")
            .requestMatchers(HttpMethod.PATCH, "/api/v1/vacancies/**").hasRole("EMPLOYER")
            .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        writeError(response, request, HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    private void writeForbidden(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        String message = accessDeniedException.getMessage() != null ? accessDeniedException.getMessage() : "Access denied";
        writeError(response, request, HttpStatus.FORBIDDEN, message);
    }

    private void writeError(HttpServletResponse response, HttpServletRequest request, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
