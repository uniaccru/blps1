package com.example.hhflow.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateResumeRequest {

        @NotNull
        private Long candidateId;

        @NotBlank
        private String fullName;

        @NotBlank
        private String summary;

        public Long getCandidateId() {
                return candidateId;
        }

        public void setCandidateId(Long candidateId) {
                this.candidateId = candidateId;
        }

        public String getFullName() {
                return fullName;
        }

        public void setFullName(String fullName) {
                this.fullName = fullName;
        }

        public String getSummary() {
                return summary;
        }

        public void setSummary(String summary) {
                this.summary = summary;
        }
}
