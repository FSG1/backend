package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

public class LayerActivityService extends Service {
    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    LayerActivityService(Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves the overview for a qualification.
     *
     * @return The query string.
     */
    public String getQueryQualificationsOverview() {
        return
                "";
    }
}
