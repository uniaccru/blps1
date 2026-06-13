package com.example.hhflow.model;

public enum Role {
    EMPLOYER,
    APPLICANT;

    /** Spring Security authority, e.g. {@code ROLE_EMPLOYER}. */
    public String authority() {
        return "ROLE_" + name();
    }
}