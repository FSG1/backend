package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

/**
 * The service class for the curricula endpoint.
 */
public class CurriculaService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    CurriculaService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves every curriculum.
     *
     * @return Query string.
     */
    public String getQueryCurriculaString() {
        return "SELECT array_to_json(array_agg(row_to_json(sp))) as curricula " +
                "FROM study.studyprogramme sp";
    }
}
