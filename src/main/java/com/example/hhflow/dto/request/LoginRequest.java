package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Вход в систему: {@code username} — номер телефона (любой пользователь) или email работодателя.
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = ValidationConstraints.EMAIL_MAX_LENGTH, message = "must be at most {max} characters")
    private String username;

    @NotBlank(message = "must not be blank")
    @Size(min = ValidationConstraints.PASSWORD_MIN_LENGTH, max = ValidationConstraints.PASSWORD_MAX_LENGTH,
            message = "must be between {min} and {max} characters")
    private String password;
}
