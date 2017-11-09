package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

/**
 * The service class for the qualifications endpoint.
 */
public class QualificationsService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    QualificationsService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query to retrieve all curricula, architectural layers and lifecycle activities.
     *
     * @return The query string.
     */
    public String getQualificationsQuery() {
        return
                "";
    }
}
