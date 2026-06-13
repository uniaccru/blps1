package com.example.odoo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OdooManagedConnection implements ManagedConnection {

    private final OdooManagedConnectionFactory managedConnectionFactory;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private OdooConnectionImpl connectionHandle;
    private boolean destroyed;

    public OdooManagedConnection(OdooManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    OdooManagedConnectionFactory getManagedConnectionFactory() {
        return managedConnectionFactory;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        if (destroyed) {
            throw new ResourceException("Managed connection is destroyed");
        }
        if (connectionHandle == null) {
            connectionHandle = new OdooConnectionImpl(managedConnectionFactory, this);
        }
        return connectionHandle;
    }

    @Override
    public void destroy() {
        destroyed = true;
        if (connectionHandle != null) {
            connectionHandle.invalidate();
            connectionHandle = null;
        }
    }

    @Override
    public void cleanup() {
        if (connectionHandle != null) {
            connectionHandle.invalidate();
            connectionHandle = null;
        }
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof OdooConnectionImpl odooConnection)) {
            throw new ResourceException("Invalid connection handle");
        }
        if (destroyed) {
            throw new ResourceException("Managed connection is destroyed");
        }
        odooConnection.associate(this);
        connectionHandle = odooConnection;
    }

    void dissociateConnection(OdooConnectionImpl connection) {
        if (connectionHandle == connection) {
            connectionHandle = null;
        }
        sendEvent(ConnectionEvent.CONNECTION_CLOSED);
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    private void sendEvent(int eventType) {
        ConnectionEvent event = new ConnectionEvent(this, eventType);
        for (ConnectionEventListener listener : listeners) {
            listener.connectionClosed(event);
        }
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() {
        return null;
    }

    @Override
    public XAResource getXAResource() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        // no-op
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }
}
