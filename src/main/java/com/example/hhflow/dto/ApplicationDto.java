package com.example.hhflow.dto;

import com.example.hhflow.domain.ApplicationStatus;

import java.time.OffsetDateTime;

public class ApplicationDto {

        private final Long id;
        private final Long vacancyId;
        private final Long resumeId;
        private final Long candidateId;
        private final ApplicationStatus status;
        private final OffsetDateTime createdAt;
        private final OffsetDateTime employerNotifiedAt;

        public ApplicationDto(
                        Long id,
                        Long vacancyId,
                        Long resumeId,
                        Long candidateId,
                        ApplicationStatus status,
                        OffsetDateTime createdAt,
                        OffsetDateTime employerNotifiedAt
        ) {
                this.id = id;
                this.vacancyId = vacancyId;
                this.resumeId = resumeId;
                this.candidateId = candidateId;
                this.status = status;
                this.createdAt = createdAt;
                this.employerNotifiedAt = employerNotifiedAt;
        }

        public Long getId() {
                return id;
        }

        public Long getVacancyId() {
                return vacancyId;
        }

        public Long getResumeId() {
                return resumeId;
        }

        public Long getCandidateId() {
                return candidateId;
        }

        public ApplicationStatus getStatus() {
                return status;
        }

        public OffsetDateTime getCreatedAt() {
                return createdAt;
        }

        public OffsetDateTime getEmployerNotifiedAt() {
                return employerNotifiedAt;
        }
}
