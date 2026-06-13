package com.example.hhflow.dto.response;

import java.time.OffsetDateTime;

import com.example.hhflow.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationDto {

        private final Long id;
        private final Long vacancyId;
        private final Long resumeId;
        private final Long candidateId;
        private final ApplicationStatus status;
        private final OffsetDateTime createdAt;
        private final OffsetDateTime employerNotifiedAt;
}
