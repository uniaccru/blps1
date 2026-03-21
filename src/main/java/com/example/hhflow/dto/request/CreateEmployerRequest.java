package com.example.hhflow.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployerRequest {

    @Email
    @NotBlank
    private String email;
}
