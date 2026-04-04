package com.example.hhflow.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResumeRequest {

        @NotNull(message = "must not be null")
        @Positive(message = "must be a positive number")
        private Long candidateId;

        @NotBlank(message = "must not be blank")
        @Size(max = 150, message = "must be at most 150 characters")
        private String fullName;

        @NotBlank(message = "must not be blank")
        @Size(max = 2000, message = "must be at most 2000 characters")
        private String summary;
}
