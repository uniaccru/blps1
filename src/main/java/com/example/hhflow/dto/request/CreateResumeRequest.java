package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
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
        @Size(max = ValidationConstraints.FULL_NAME_MAX_LENGTH, message = "must be at most {max} characters")
        private String fullName;

        @NotBlank(message = "must not be blank")
        @Size(max = ValidationConstraints.SUMMARY_MAX_LENGTH, message = "must be at most {max} characters")
        private String summary;
}
