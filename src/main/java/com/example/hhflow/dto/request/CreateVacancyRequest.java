package com.example.hhflow.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateVacancyRequest {

        @NotBlank
        private String title;

        @NotNull
        private Boolean requiresTest;

        @Email
        @NotBlank
        private String employerEmail;

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public Boolean getRequiresTest() {
                return requiresTest;
        }

        public void setRequiresTest(Boolean requiresTest) {
                this.requiresTest = requiresTest;
        }

        public String getEmployerEmail() {
                return employerEmail;
        }

        public void setEmployerEmail(String employerEmail) {
                this.employerEmail = employerEmail;
        }
}
