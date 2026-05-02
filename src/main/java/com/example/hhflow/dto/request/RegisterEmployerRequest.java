package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterEmployerRequest extends RegisterRequest {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    @Size(max = ValidationConstraints.EMAIL_MAX_LENGTH, message = "must be at most {max} characters")
    private String email;
}
