package com.example.odoo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.resource.spi.ValidatingManagedConnectionFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class OdooManagedConnectionFactory
        implements ManagedConnectionFactory, Serializable, Referenceable, ValidatingManagedConnectionFactory {

    @Serial
    private static final long serialVersionUID = 1L;

    private String baseUrl = "http://localhost:8069";
    private String database = "odoo";
    private String username = "admin";
    private String password = "admin";
    private PrintWriter logWriter;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return createConnectionFactory(null);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException {
        return new OdooConnectionFactoryImpl(this, connectionManager);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo)
            throws ResourceException {
        return new OdooManagedConnection(this);
    }

    @Override
    public ManagedConnection matchManagedConnections(
            Set candidateConnections,
            Subject subject,
            ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        for (Object candidate : candidateConnections) {
            if (candidate instanceof OdooManagedConnection managedConnection
                    && managedConnection.getManagedConnectionFactory().equals(this)) {
                return managedConnection;
            }
        }
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public Reference getReference() throws NamingException {
        Reference reference = new Reference(
                OdooManagedConnectionFactory.class.getName(),
                OdooManagedConnectionFactory.class.getName(),
                null
        );
        reference.add(new StringRefAddr("baseUrl", baseUrl));
        reference.add(new StringRefAddr("database", database));
        reference.add(new StringRefAddr("username", username));
        reference.add(new StringRefAddr("password", password));
        return reference;
    }

    @Override
    public Set getInvalidConnections(Set connectionSet) throws ResourceException {
        return Set.of();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OdooManagedConnectionFactory that)) {
            return false;
        }
        return baseUrl.equals(that.baseUrl)
                && database.equals(that.database)
                && username.equals(that.username)
                && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        int result = baseUrl.hashCode();
        result = 31 * result + database.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
