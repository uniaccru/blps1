package com.example.hhflow.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitApplicationRequest {

        @NotNull
        private Long vacancyId;

        @NotNull
        private Long candidateId;

        private Long resumeId;

        private String resumeFullName;

        private String resumeSummary;

        private Boolean simulateResumeCreationSuccess;

        private Boolean simulateTestPassed;
}
