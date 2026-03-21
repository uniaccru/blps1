package com.example.hhflow.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVacancyRequest {

        @NotBlank
        private String title;

        @NotNull
        private Boolean requiresTest;

        @Email
        @NotBlank
        private String employerEmail;
}
