package com.example.odoo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;

import javax.security.auth.Subject;

public class OdooConnectionFactoryImpl implements OdooConnectionFactory {

    private final OdooManagedConnectionFactory managedConnectionFactory;
    private final ConnectionManager connectionManager;

    public OdooConnectionFactoryImpl(
            OdooManagedConnectionFactory managedConnectionFactory,
            ConnectionManager connectionManager) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionManager = connectionManager;
    }

    @Override
    public OdooConnection getConnection() throws ResourceException {
        ManagedConnection managedConnection;
        if (connectionManager != null) {
            managedConnection = (ManagedConnection) connectionManager.allocateConnection(
                    managedConnectionFactory,
                    (ConnectionRequestInfo) null
            );
        } else {
            managedConnection = managedConnectionFactory.createManagedConnection(
                    (Subject) null,
                    (ConnectionRequestInfo) null
            );
        }
        return (OdooConnection) managedConnection.getConnection(null, null);
    }
}
