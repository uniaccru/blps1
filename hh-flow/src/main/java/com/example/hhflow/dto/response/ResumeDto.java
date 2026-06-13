package com.example.hhflow.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumeDto {

        private final Long id;
        private final Long candidateId;
        private final String fullName;
        private final String summary;
        private final OffsetDateTime createdAt;
}
