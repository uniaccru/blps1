package com.example.hhflow.dto.error;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

        private final OffsetDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;
}
