package com.example.odoo.connector;

import jakarta.resource.ResourceException;

public interface OdooConnectionFactory {

    OdooConnection getConnection() throws ResourceException;
}
