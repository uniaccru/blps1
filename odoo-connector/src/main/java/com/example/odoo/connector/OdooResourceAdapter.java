package com.example.odoo.connector;

import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;
import java.io.Serial;
import java.io.Serializable;

public class OdooResourceAdapter implements ResourceAdapter, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void start(BootstrapContext ctx) {
        // no-op
    }

    @Override
    public void stop() {
        // no-op
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        // outbound-only adapter
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        // outbound-only adapter
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) {
        return new XAResource[0];
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof OdooResourceAdapter;
    }

    @Override
    public int hashCode() {
        return OdooResourceAdapter.class.hashCode();
    }
}
