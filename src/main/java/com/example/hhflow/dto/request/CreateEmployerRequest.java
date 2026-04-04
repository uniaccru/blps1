package com.example.hhflow.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployerRequest {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email address")
    @Size(max = 255, message = "must be at most 255 characters")
    private String email;
}
