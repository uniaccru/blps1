package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
