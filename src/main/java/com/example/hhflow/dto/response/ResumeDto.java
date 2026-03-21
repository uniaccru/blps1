package com.example.hhflow.dto.response;

import java.time.OffsetDateTime;

public class ResumeDto {

        private final Long id;
        private final Long candidateId;
        private final String fullName;
        private final String summary;
        private final OffsetDateTime createdAt;

        public ResumeDto(Long id, Long candidateId, String fullName, String summary, OffsetDateTime createdAt) {
                this.id = id;
                this.candidateId = candidateId;
                this.fullName = fullName;
                this.summary = summary;
                this.createdAt = createdAt;
        }

        public Long getId() {
                return id;
        }

        public Long getCandidateId() {
                return candidateId;
        }

        public String getFullName() {
                return fullName;
        }

        public String getSummary() {
                return summary;
        }

        public OffsetDateTime getCreatedAt() {
                return createdAt;
        }
}
