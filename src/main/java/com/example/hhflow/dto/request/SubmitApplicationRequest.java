package com.example.hhflow.dto.request;

import com.example.hhflow.validation.ValidationConstraints;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
