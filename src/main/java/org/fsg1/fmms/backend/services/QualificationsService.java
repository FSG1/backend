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
                "WITH " +
                        "  activities as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.activity), " +
                        "  als as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.architecturallayer) " +
                        "SELECT json_build_object( " +
                        "  'curricula', (SELECT array_to_json(array_agg(row_to_json(sp))) from study.studyprogramme sp), " +
                        "  'architectural_layers', (select json from als), " +
                        "  'lifecycle_activities', (select json from activities) " +
                        ") as qualifications;";
    }
}
