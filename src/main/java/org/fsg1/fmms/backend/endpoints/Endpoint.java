package org.fsg1.fmms.backend.endpoints;

import org.fsg1.fmms.backend.services.Service;

/**
 * Abstract class for an Endpoint.
 */
public abstract class Endpoint {
    private final Service service;

    public Service getService() {
        return service;
    }

    /**
     * Constructor which receives the service as dependency.
     *
     * @param service Service object.
     */
    Endpoint(final Service service) {
        this.service = service;
    }
}
