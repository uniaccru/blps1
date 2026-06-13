package com.example.odoo.connector;

import jakarta.resource.ResourceException;

public interface OdooConnection extends AutoCloseable {

    int createApplicant(ApplicantData data) throws ResourceException;

    @Override
    void close();
}
