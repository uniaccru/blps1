package com.example.hhflow.dto;

import com.example.hhflow.domain.SubmissionOutcome;

public class SubmissionResponse {

        private final SubmissionOutcome outcome;
        private final String message;
        private final ApplicationDto application;

        public SubmissionResponse(SubmissionOutcome outcome, String message, ApplicationDto application) {
                this.outcome = outcome;
                this.message = message;
                this.application = application;
        }

        public SubmissionOutcome getOutcome() {
                return outcome;
        }

        public String getMessage() {
                return message;
        }

        public ApplicationDto getApplication() {
                return application;
        }
}
