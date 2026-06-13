package com.example.odoo.connector;

import java.time.OffsetDateTime;

public record ApplicantData(
        String candidateName,
        String email,
        String vacancyTitle,
        OffsetDateTime appliedAt,
        String resumeContent
) {
}
