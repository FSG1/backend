package org.fsg1.fmms.backend.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * A filter class for Responses that add the required CORS Headers to enable CORS.
 */
public class CORSResponseFilter
        implements ContainerResponseFilter {

    /**
     * {@inheritDoc}
     * <p>
     * Filters responses to add CORS Headers.
     */
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext)
            throws IOException {

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
    }
}
