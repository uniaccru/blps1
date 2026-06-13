package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Вход в систему: email (для всех пользователей).
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    @Size(max = ValidationConstraints.EMAIL_MAX_LENGTH, message = "must be at most {max} characters")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(min = ValidationConstraints.PASSWORD_MIN_LENGTH, max = ValidationConstraints.PASSWORD_MAX_LENGTH,
            message = "must be between {min} and {max} characters")
    private String password;
}
