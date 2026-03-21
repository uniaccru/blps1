package com.example.hhflow.dto.request;

import javax.validation.constraints.NotNull;

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

        public Long getVacancyId() {
                return vacancyId;
        }

        public void setVacancyId(Long vacancyId) {
                this.vacancyId = vacancyId;
        }

        public Long getCandidateId() {
                return candidateId;
        }

        public void setCandidateId(Long candidateId) {
                this.candidateId = candidateId;
        }

        public Long getResumeId() {
                return resumeId;
        }

        public void setResumeId(Long resumeId) {
                this.resumeId = resumeId;
        }

        public String getResumeFullName() {
                return resumeFullName;
        }

        public void setResumeFullName(String resumeFullName) {
                this.resumeFullName = resumeFullName;
        }

        public String getResumeSummary() {
                return resumeSummary;
        }

        public void setResumeSummary(String resumeSummary) {
                this.resumeSummary = resumeSummary;
        }

        public Boolean getSimulateResumeCreationSuccess() {
                return simulateResumeCreationSuccess;
        }

        public void setSimulateResumeCreationSuccess(Boolean simulateResumeCreationSuccess) {
                this.simulateResumeCreationSuccess = simulateResumeCreationSuccess;
        }

        public Boolean getSimulateTestPassed() {
                return simulateTestPassed;
        }

        public void setSimulateTestPassed(Boolean simulateTestPassed) {
                this.simulateTestPassed = simulateTestPassed;
        }
}
