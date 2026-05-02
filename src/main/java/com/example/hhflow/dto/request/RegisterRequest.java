package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
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
    @Size(max = ValidationConstraints.PHONE_MAX_LENGTH, message = "must be at most {max} characters")
    private String phone;

    @NotBlank(message = "must not be blank")
    @Size(min = ValidationConstraints.PASSWORD_MIN_LENGTH, max = ValidationConstraints.PASSWORD_MAX_LENGTH,
            message = "must be between {min} and {max} characters")
    private String password;
}
