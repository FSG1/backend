package org.fsg1.fmms.backend.filters;

import org.fsg1.fmms.backend.app.Configuration;
import org.fsg1.fmms.backend.exceptions.UnauthorizedException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Authentication Filters.
 * Enforces authentication for everything which tries to modify data
 *
 * @author Tobias Derksen
 */
@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {

    /**
     * Regular Expression to detect a valid Authorization header.
     */
    private static final String REGEX = "^Basic\\s([A-Za-z0-9+/=-]+)$";

    private final Configuration config;

    /**
     * Constructor.
     *
     * @param config App configuration
     */
    @Inject
    public AuthFilter(final Configuration config) {
        this.config = config;
    }

    /**
     * Apply the filter : check input request, validate or not with user auth.
     *
     * @param containerRequest The request from server
     */
    @Override
    public void filter(final ContainerRequestContext containerRequest) throws UnauthorizedException {
        if (!containerRequest.getMethod().equals("GET") && !containerRequest.getMethod().equals("OPTIONS")) {
            String header = containerRequest.getHeaderString("Authorization");
            if (header != null && !header.isEmpty()) {
                Pattern pattern = Pattern.compile(REGEX);
                Matcher m = pattern.matcher(header.trim());

                if (m.find()) {
                    String base64 = m.group(1);
                    String decoded = new String(Base64.getDecoder().decode(base64));
                    String[] credentials = decoded.split(":");

                    if (credentials.length == 2) {
                        String username = credentials[0].trim();
                        String password = credentials[1].trim();

                        if (this.config.getAuthUsername().equals(username) && this.config.getAuthPassword().equals(password)) {
                            return;
                        }
                    }
                }
            }

            throw new UnauthorizedException();
        }
    }
}
