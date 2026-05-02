package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Общие поля регистрации; для работодателя см. {@link RegisterEmployerRequest}, для соискателя {@link RegisterApplicantRequest}.
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    @Size(max = ValidationConstraints.EMAIL_MAX_LENGTH, message = "must be at most {max} characters")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(min = ValidationConstraints.PASSWORD_MIN_LENGTH, max = ValidationConstraints.PASSWORD_MAX_LENGTH,
            message = "must be between {min} and {max} characters")
    private String password;
}
