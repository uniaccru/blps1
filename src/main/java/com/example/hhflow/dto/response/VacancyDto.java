package com.example.hhflow.dto.response;

import com.example.hhflow.domain.VacancyStatus;

public class VacancyDto {

        private final Long id;
        private final String title;
        private final VacancyStatus status;
        private final boolean requiresTest;
        private final String employerEmail;

        public VacancyDto(Long id, String title, VacancyStatus status, boolean requiresTest, String employerEmail) {
                this.id = id;
                this.title = title;
                this.status = status;
                this.requiresTest = requiresTest;
                this.employerEmail = employerEmail;
        }

        public Long getId() {
                return id;
        }

        public String getTitle() {
                return title;
        }

        public VacancyStatus getStatus() {
                return status;
        }

        public boolean isRequiresTest() {
                return requiresTest;
        }

        public String getEmployerEmail() {
                return employerEmail;
        }
}
