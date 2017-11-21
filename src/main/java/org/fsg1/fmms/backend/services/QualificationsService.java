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
                        "  activities AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "  als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer) " +
                        "SELECT Json_build_object( " +
                        "  'curricula', (SELECT Array_to_json(Array_agg(Row_to_json(sp))) FROM study.studyprogramme sp), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'lifecycle_activities', (SELECT json FROM activities) " +
                        ") AS qualifications;";
    }
}
