package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitApplicationRequest {

        @NotNull(message = "must not be null")
        @Positive(message = "must be a positive number")
        private Long vacancyId;

        @Positive(message = "must be a positive number")
        private Long resumeId;

        @Size(max = ValidationConstraints.FULL_NAME_MAX_LENGTH, message = "must be at most {max} characters")
        private String resumeFullName;

        @Size(max = ValidationConstraints.SUMMARY_MAX_LENGTH, message = "must be at most {max} characters")
        private String resumeSummary;
}
