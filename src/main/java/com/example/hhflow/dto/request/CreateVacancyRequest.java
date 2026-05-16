package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVacancyRequest {

        @NotBlank(message = "must not be blank")
        @Size(max = ValidationConstraints.TITLE_MAX_LENGTH, message = "must be at most {max} characters")
        private String title;

        @NotNull(message = "must not be null")
        private Boolean requiresTest;
}
