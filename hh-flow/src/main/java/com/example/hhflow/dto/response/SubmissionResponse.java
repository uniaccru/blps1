package com.example.hhflow.dto.response;

import com.example.hhflow.model.SubmissionOutcome;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmissionResponse {

        private final SubmissionOutcome outcome;
        private final String message;
        private final ApplicationDto application;
}
