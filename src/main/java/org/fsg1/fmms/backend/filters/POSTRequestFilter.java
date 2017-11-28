package org.fsg1.fmms.backend.filters;

import org.fsg1.fmms.backend.exceptions.EmptyRequestBodyException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

/**
 * Filter for all POST requests.
 */
public class POSTRequestFilter implements ContainerRequestFilter {

    /**
     * Filter to check POST requests for their request bodies. If there is an empty or null body,
     * throw an error.
     *
     * @param requestContext request context.
     * @throws IOException if an I/O exception occurs.
     */
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getMethod().equals("POST") && requestContext.getLength() <= 0) {
            throw new EmptyRequestBodyException();
        }
    }
}
