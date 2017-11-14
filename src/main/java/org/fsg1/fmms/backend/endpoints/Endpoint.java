package org.fsg1.fmms.backend.endpoints;

import org.fsg1.fmms.backend.services.Service;

/**
 * Abstract class for an Endpoint.
 * @param <ServiceType> Type of Service this endpoint uses.
 */
public abstract class Endpoint<ServiceType extends Service> {
    private final ServiceType service;

    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    Endpoint(final ServiceType service) {
        this.service = service;
    }

    /**
     * Gets the service of the Endpoint.
     *
     * @return the service.
     */
    public ServiceType getService() {
        return service;
    }
}
