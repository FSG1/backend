package org.fsg1.fmms.backend.endpoints;

import org.fsg1.fmms.backend.services.Service;

/**
 * Abstract class for an Endpoint.
 */
public abstract class Endpoint {
    private final Service service;

    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    Endpoint(final Service service) {
        this.service = service;
    }

    /**
     * Gets the service of the Endpoint.
     *
     * @return the service.
     */
    public Service getService() {
        return service;
    }
}
