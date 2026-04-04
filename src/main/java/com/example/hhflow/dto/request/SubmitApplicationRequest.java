package com.example.hhflow.dto.request;

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

        @NotNull(message = "must not be null")
        @Positive(message = "must be a positive number")
        private Long candidateId;

        @Positive(message = "must be a positive number")
        private Long resumeId;

        @Size(max = 150, message = "must be at most 150 characters")
        private String resumeFullName;

        @Size(max = 2000, message = "must be at most 2000 characters")
        private String resumeSummary;

        private Boolean simulateAuthorized;

        private Boolean simulateRegistrationSuccess;

        private Boolean simulateResumeCreationSuccess;

        private Boolean simulateTestPassed;
}
