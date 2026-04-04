package com.example.hhflow.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVacancyRequest {

        @NotBlank(message = "must not be blank")
        @Size(max = 150, message = "must be at most 150 characters")
        private String title;

        @NotNull(message = "must not be null")
        private Boolean requiresTest;

        @NotNull(message = "must not be null")
        @Positive(message = "must be a positive number")
        private Long employerId;
}
