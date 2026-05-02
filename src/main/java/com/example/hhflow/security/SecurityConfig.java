package com.example.hhflow.security;

import com.example.hhflow.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.hhflow.security.jwt.JwtAuthenticationFilter;
import com.example.hhflow.security.jwt.JwtProperties;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling()
                .authenticationEntryPoint(this::writeUnauthorized)
                .accessDeniedHandler(this::writeForbidden);
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/vacancies", "/api/v1/vacancies/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/applications/submit").hasRole("APPLICANT")
                .antMatchers("/api/v1/resumes/**").hasRole("APPLICANT")
                .antMatchers(HttpMethod.GET,
                        "/api/v1/applications",
                        "/api/v1/applications/*").hasRole("EMPLOYER")
                .antMatchers(HttpMethod.POST, "/api/v1/vacancies").hasRole("EMPLOYER")
                .antMatchers(HttpMethod.PATCH, "/api/v1/vacancies/**").hasRole("EMPLOYER")
                .anyRequest().authenticated();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException) throws IOException {
        writeError(response, request, HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    private void writeForbidden(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
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
